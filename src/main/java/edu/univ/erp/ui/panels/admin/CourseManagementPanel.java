package edu.univ.erp.ui.panels.admin;

import edu.univ.erp.domain.Course;

import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import edu.univ.erp.service.AdminService;

public class CourseManagementPanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private DefaultTableModel model;
    private JTable table;

    public CourseManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        loadCourses(); // << correct load
    }

    // ---------- HEADER ----------
    private JComponent buildHeader() {
        JLabel title = new JLabel("Course Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return title;
    }

    // ---------- TABLE ----------
    private JComponent buildTableArea() {
        TitledContainer container = new TitledContainer("Course List");

        model = new DefaultTableModel(
                new Object[] { "ID", "Course Code", "Title", "Credits" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFillsViewportHeight(true);
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

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        container.setInnerComponent(scroll);
        return container;
    }

    // ---------- ACTION BUTTONS ----------
    private JComponent buildActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setOpaque(false);

        RoundedButton addBtn = new RoundedButton("Add Course", new Color(46, 204, 113));
        RoundedButton editBtn = new RoundedButton("Edit Selected", new Color(52, 152, 219));
        RoundedButton deleteBtn = new RoundedButton("Delete Selected", new Color(231, 76, 60));

        addBtn.addActionListener(e -> onAddCourse());
        editBtn.addActionListener(e -> onEditCourse());
        deleteBtn.addActionListener(e -> {
            try {
                onDeleteCourse();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        return panel;
    }

    // ---------- LOAD COURSES ----------
    private void loadCourses() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    List<Course> list = adminService.getAllCourses();
                    SwingUtilities.invokeLater(() -> {
                        model.setRowCount(0);
                        for (Course c : list) {
                            model.addRow(new Object[] {
                                    c.getCourseid(),
                                    c.getCode(),
                                    c.getTitle(),
                                    c.getCredits()
                            });
                        }
                    });
                } catch (Exception e) {
                    // Silent failure during initialization - table remains empty
                    SwingUtilities.invokeLater(() -> model.setRowCount(0));
                }
                return null;
            }
        };
        worker.execute();
    }

    // ---------- ADD COURSE ----------
    private void onAddCourse() {
        JTextField codeField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField creditsField = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Course Code:"));
        p.add(codeField);
        p.add(new JLabel("Title:"));
        p.add(titleField);
        p.add(new JLabel("Credits:"));
        p.add(creditsField);

        int result = JOptionPane.showConfirmDialog(
                this, p, "Add Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validation
                String code = codeField.getText().trim();
                String title = titleField.getText().trim();
                if (code.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Course Code is required.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title is required.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int credits;
                try {
                    credits = Integer.parseInt(creditsField.getText().trim());
                    if (credits <= 0) {
                        JOptionPane.showMessageDialog(this, "Credits must be greater than 0.", "Validation Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Credits must be a valid number.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Course c = new Course();
                c.setCode(code);
                c.setTitle(title);
                c.setCredits(credits);

                if (adminService.createCourse(c)) {
                    JOptionPane.showMessageDialog(this, "Course created successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create course.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error creating course: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ---------- EDIT COURSE ----------
    private void onEditCourse() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String oldCode = (String) model.getValueAt(row, 1);
        String oldTitle = (String) model.getValueAt(row, 2);
        String oldCredits = String.valueOf(model.getValueAt(row, 3));

        JTextField codeField = new JTextField(oldCode);
        JTextField titleField = new JTextField(oldTitle);
        JTextField creditsField = new JTextField(oldCredits);

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Course Code:"));
        p.add(codeField);
        p.add(new JLabel("Title:"));
        p.add(titleField);
        p.add(new JLabel("Credits:"));
        p.add(creditsField);

        int result = JOptionPane.showConfirmDialog(
                this, p, "Edit Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validation
                String code = codeField.getText().trim();
                String title = titleField.getText().trim();
                if (code.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Course Code is required.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title is required.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int credits;
                try {
                    credits = Integer.parseInt(creditsField.getText().trim());
                    if (credits <= 0) {
                        JOptionPane.showMessageDialog(this, "Credits must be greater than 0.", "Validation Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Credits must be a valid number.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Course updated = new Course();
                updated.setCourseid(id);
                updated.setCode(code);
                updated.setTitle(title);
                updated.setCredits(credits);

                if (adminService.updateCourse(updated)) {
                    JOptionPane.showMessageDialog(this, "Course updated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update course.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error updating course: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ---------- DELETE COURSE ----------
    private void onDeleteCourse() throws Exception {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course first.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete this course?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (adminService.deleteCourse(id)) {
                JOptionPane.showMessageDialog(this, "Course deleted.");
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Unable to delete course.");
            }
        }
    }
}
