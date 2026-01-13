package edu.univ.erp.ui.panels.student;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GradesPanel extends JPanel {
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final GradeDAO gradeDAO = new GradeDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    private DefaultTableModel model;

    public GradesPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        TitledContainer container = new TitledContainer("My Grades");

        model = new DefaultTableModel(new Object[] {
                "Course Code", "Course Title", "Component", "Score", "Final Grade"
        }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(236, 236, 236));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));

        // Zebra striping
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : Color.WHITE);
                }
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomPanel.setOpaque(false);

        RoundedButton refreshBtn = new RoundedButton("Refresh", new Color(52, 152, 219));
        refreshBtn.addActionListener(e -> loadGrades());
        bottomPanel.add(refreshBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        wrapper.add(scroll, BorderLayout.CENTER);
        wrapper.add(bottomPanel, BorderLayout.SOUTH);

        container.setInnerComponent(wrapper);
        add(container, BorderLayout.CENTER);

        loadGrades();
    }

    private void loadGrades() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    model.setRowCount(0);

                    int studentId = SessionManager.getCurrentUserId();
                    List<Enrollment> enrollments = enrollmentDAO.getByStudent(studentId);

                    for (Enrollment enrollment : enrollments) {
                        Section section = sectionDAO.getById(enrollment.getSectionid());
                        if (section == null)
                            continue;

                        Course course = courseDAO.getById(section.getCourseid());
                        if (course == null)
                            continue;

                        List<Grade> grades = gradeDAO.getByEnrollment(enrollment.getEnrollmentid());

                        if (grades.isEmpty()) {
                            model.addRow(new Object[] {
                                    course.getCode(),
                                    course.getTitle(),
                                    "No grades yet",
                                    "-",
                                    "-"
                            });
                        } else {
                            for (Grade grade : grades) {
                                model.addRow(new Object[] {
                                        course.getCode(),
                                        course.getTitle(),
                                        grade.getComponent(),
                                        String.format("%.2f", grade.getScore()),
                                        grade.getFinalgrade() != null ? grade.getFinalgrade() : "-"
                                });
                            }
                        }
                    }

                    if (enrollments.isEmpty()) {
                        model.addRow(new Object[] { "No enrollments", "", "", "", "" });
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(GradesPanel.this,
                            "Error loading grades: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE));
                }
                return null;
            }
        };
        worker.execute();
    }
}
