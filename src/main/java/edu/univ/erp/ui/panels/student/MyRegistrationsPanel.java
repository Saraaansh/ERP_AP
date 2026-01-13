package edu.univ.erp.ui.panels.student;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class MyRegistrationsPanel extends JPanel {
    private final StudentService studentService = new StudentService();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();

    private DefaultTableModel model;
    private JTable table;

    public MyRegistrationsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        TitledContainer container = new TitledContainer("My Registrations");

        model = new DefaultTableModel(new Object[] {
                "Enrollment ID", "Course Code", "Title", "Section", "Instructor", "Day/Time", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        RoundedButton dropBtn = new RoundedButton("Drop Selected", new Color(231, 76, 60));
        RoundedButton refreshBtn = new RoundedButton("Refresh", new Color(52, 152, 219));

        dropBtn.addActionListener(e -> dropSection());
        refreshBtn.addActionListener(e -> loadRegistrations());

        bottomPanel.add(refreshBtn);
        bottomPanel.add(dropBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        wrapper.add(scroll, BorderLayout.CENTER);
        wrapper.add(bottomPanel, BorderLayout.SOUTH);

        container.setInnerComponent(wrapper);
        add(container, BorderLayout.CENTER);

        loadRegistrations();

        // Auto-refresh when this panel becomes visible (e.g. switching from Catalog)
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadRegistrations();
            }
        });
    }

    private void loadRegistrations() {
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

                        Instructor instructor = instructorDAO.getById(section.getInstructorid());
                        String instrName = (instructor != null) ? instructor.getName() : "TBA";

                        model.addRow(new Object[] {
                                enrollment.getEnrollmentid(),
                                course.getCode(),
                                course.getTitle(),
                                "SEC-" + section.getSectionid(),
                                instrName,
                                section.getDaytime() != null ? section.getDaytime() : "TBA",
                                enrollment.getStatus()
                        });
                    }

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(MyRegistrationsPanel.this,
                            "Error loading registrations: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE));
                }
                return null;
            }
        };
        worker.execute();
    }

    private void dropSection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a registration to drop.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Maintenance check
        if (!edu.univ.erp.service.MaintenanceService.checkMaintenance(this)) {
            return;
        }

        String status = (String) model.getValueAt(selectedRow, 6);
        if ("dropped".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                    "This course has already been dropped.",
                    "Already Dropped", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int enrollmentId = (int) model.getValueAt(selectedRow, 0);
        String courseCode = (String) model.getValueAt(selectedRow, 1);
        String courseTitle = (String) model.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Drop " + courseCode + " - " + courseTitle + "?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String errorMsg = null;

            @Override
            protected Boolean doInBackground() {
                try {
                    int studentId = SessionManager.getCurrentUserId();
                    // Get section ID from enrollment
                    Enrollment e = enrollmentDAO.getById(enrollmentId);
                    if (e == null) {
                        errorMsg = "Enrollment not found.";
                        return false;
                    }
                    return studentService.dropSection(studentId, e.getSectionid());
                } catch (Exception ex) {
                    errorMsg = ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(MyRegistrationsPanel.this,
                                "Successfully dropped " + courseCode + ".",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadRegistrations();
                    } else {
                        JOptionPane.showMessageDialog(MyRegistrationsPanel.this,
                                "Drop failed: " + (errorMsg != null ? errorMsg : "Unknown error"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MyRegistrationsPanel.this,
                            "Drop failed: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
