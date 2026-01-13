package edu.univ.erp.ui.panels.instructor;

import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;
import edu.univ.erp.ui.service.InstructorDashboardDataProvider;
import edu.univ.erp.ui.service.InstructorDashboardDataProvider.StudentRow;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Panel that shows enrolled students for a selected section.
 * Uses InstructorDashboardDataProvider.loadStudentsInSection(String sectionId).
 */
public class InstructorCourseStudentsPanel extends JPanel {

    private final InstructorDashboardDataProvider provider;

    private final JLabel headerLabel = new JLabel("Students");
    private final DefaultTableModel studentModel;
    private final JTable studentTable;
    private String currentSectionId = null;
    private final RoundedButton refreshBtn = new RoundedButton("Refresh", new Color(52, 152, 219));

    public InstructorCourseStudentsPanel(InstructorDashboardDataProvider provider) {
        this.provider = provider;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerLabel.setForeground(new Color(44, 62, 80));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        studentModel = new DefaultTableModel(new Object[] { "Student ID", "Name" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        studentTable = new JTable(studentModel);
        studentTable.setRowHeight(32);
        studentTable.setShowVerticalLines(false);
        studentTable.setIntercellSpacing(new Dimension(0, 0));

        // Header styling
        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(236, 236, 236));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));

        // Zebra striping
        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        top.setOpaque(false);
        top.add(refreshBtn);
        refreshBtn.addActionListener(e -> {
            if (currentSectionId != null)
                loadSection(currentSectionId, null);
        });

        TitledContainer container = new TitledContainer("Enrolled Students");
        JScrollPane scroll = new JScrollPane(studentTable);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);
        wrapper.add(top, BorderLayout.SOUTH);

        container.setInnerComponent(wrapper);
        add(container, BorderLayout.CENTER);
    }

    /**
     * Called by MainFrame when a section is selected.
     * sectionId: id as string (matching provider API).
     * label: human readable label (may be null) — if provided, panel header
     * updates.
     */
    public void loadSection(String sectionId, String label) {
        this.currentSectionId = sectionId;
        if (label != null && !label.isEmpty()) {
            headerLabel.setText("Students — " + label);
        } else {
            headerLabel.setText("Students");
        }
        loadStudentsAsync(sectionId);
    }

    private void loadStudentsAsync(String sectionId) {
        studentModel.setRowCount(0);
        SwingWorker<List<StudentRow>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<StudentRow> doInBackground() throws Exception {
                return provider.loadStudentsInSection(sectionId);
            }

            @Override
            protected void done() {
                try {
                    List<StudentRow> students = get();
                    if (students == null || students.isEmpty()) {
                        studentModel.setRowCount(0);
                        studentModel.addRow(new Object[] { "(no students)", "" });
                        return;
                    }
                    studentModel.setRowCount(0);
                    for (StudentRow s : students) {
                        studentModel.addRow(new Object[] { s.getStudentId(), s.getName() });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(InstructorCourseStudentsPanel.this,
                            "Unable to load students:\n" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
