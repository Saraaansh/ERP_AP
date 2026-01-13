package edu.univ.erp.ui.panels.admin;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.InstructorDAO;

import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class SectionManagementPanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private DefaultTableModel model;
    private JTable table;

    public SectionManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        loadSectionsFromDb();
    }

    private JComponent buildHeader() {
        JLabel title = new JLabel("Section Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return title;
    }

    private JComponent buildTableArea() {
        TitledContainer container = new TitledContainer("Section List");

        model = new DefaultTableModel(
                new Object[] { "Section ID", "Course Code", "Instructor", "Semester", "Year", "Capacity" }, 0) {
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

    private JComponent buildActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setOpaque(false);

        RoundedButton addBtn = new RoundedButton("Add Section", new Color(46, 204, 113));
        RoundedButton editBtn = new RoundedButton("Edit Selected", new Color(52, 152, 219));
        RoundedButton deleteBtn = new RoundedButton("Delete Selected", new Color(231, 76, 60));

        addBtn.addActionListener(e -> onAddSection());
        editBtn.addActionListener(e -> onEditSection());
        deleteBtn.addActionListener(e -> onDeleteSection());

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        return panel;
    }

    private void onAddSection() {
        JTextField courseCodeField = new JTextField();
        JTextField instructorField = new JTextField();
        JTextField semesterField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField capacityField = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Course Code:"));
        p.add(courseCodeField);
        p.add(new JLabel("Instructor Username:"));
        p.add(instructorField);
        p.add(new JLabel("Semester:"));
        p.add(semesterField);
        p.add(new JLabel("Year:"));
        p.add(yearField);
        p.add(new JLabel("Capacity:"));
        p.add(capacityField);

        int result = JOptionPane.showConfirmDialog(
                this, p, "Add Section", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION)
            return;

        try {
            // Input Validation
            if (courseCodeField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course Code is required.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (instructorField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Instructor Username is required.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (semesterField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semester is required.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int year;
            try {
                year = Integer.parseInt(yearField.getText().trim());
                if (year < 2000 || year > 2100) {
                    JOptionPane.showMessageDialog(this, "Year must be between 2000 and 2100.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Year must be a valid number.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int capacity;
            try {
                capacity = Integer.parseInt(capacityField.getText().trim());
                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(this, "Capacity must be greater than 0.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Capacity must be a valid number.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            CourseDAO cdao = new CourseDAO();
            List<Course> allCourses = cdao.getAll();
            Course selectedCourse = allCourses.stream()
                    .filter(c -> c.getCode().equalsIgnoreCase(courseCodeField.getText().trim()))
                    .findFirst()
                    .orElseThrow(
                            () -> new Exception("Course not found with code: " + courseCodeField.getText().trim()));

            InstructorDAO idao = new InstructorDAO();
            List<Instructor> allInstructors = idao.getAll();
            Instructor selectedInstructor = allInstructors.stream()
                    .filter(i -> i.getName().equalsIgnoreCase(instructorField.getText().trim()))
                    .findFirst()
                    .orElseThrow(() -> new Exception(
                            "Instructor not found with username: " + instructorField.getText().trim()));

            Section s = new Section();
            s.setCourseid(selectedCourse.getCourseid());
            s.setInstructorid(selectedInstructor.getUserid());
            s.setSemester(semesterField.getText().trim());
            s.setYear(year);
            s.setCapacity(capacity);

            boolean ok = adminService.createSection(s);

            if (ok) {
                loadSectionsFromDb();
                JOptionPane.showMessageDialog(this, "Section created successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create section.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEditSection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a section first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int sectionId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String semester = model.getValueAt(row, 3).toString();
        String year = model.getValueAt(row, 4).toString();
        String capacity = model.getValueAt(row, 5).toString();

        JTextField semField = new JTextField(semester);
        JTextField yearField = new JTextField(year);
        JTextField capField = new JTextField(capacity);

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Semester:"));
        p.add(semField);
        p.add(new JLabel("Year:"));
        p.add(yearField);
        p.add(new JLabel("Capacity:"));
        p.add(capField);

        int result = JOptionPane.showConfirmDialog(
                this, p, "Edit Section", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION)
            return;

        try {
            // Input validation
            String newSemester = semField.getText().trim();
            if (newSemester.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semester cannot be empty.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int newYear;
            try {
                newYear = Integer.parseInt(yearField.getText().trim());
                if (newYear < 2000 || newYear > 2100) {
                    JOptionPane.showMessageDialog(this, "Year must be between 2000 and 2100.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Year must be a valid number.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int newCapacity;
            try {
                newCapacity = Integer.parseInt(capField.getText().trim());
                if (newCapacity <= 0) {
                    JOptionPane.showMessageDialog(this, "Capacity must be greater than 0.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Capacity must be a valid number.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get section and check enrolled count
            List<Section> all = adminService.getAllSections();
            Section found = all.stream()
                    .filter(s -> s.getSectionid() == sectionId)
                    .findFirst().orElseThrow(() -> new Exception("Section not found."));

            // Check capacity vs enrolled
            int currentEnrolled = new edu.univ.erp.data.SectionDAO().countEnrolled(sectionId);
            if (newCapacity < currentEnrolled) {
                JOptionPane.showMessageDialog(this,
                        "Capacity cannot be less than current enrolled count (" + currentEnrolled + ").",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            found.setSemester(newSemester);
            found.setYear(newYear);
            found.setCapacity(newCapacity);

            boolean ok = adminService.updateSection(found);
            if (ok) {
                loadSectionsFromDb();
                JOptionPane.showMessageDialog(this, "Section updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Unable to update section.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteSection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a section to delete.");
            return;
        }

        int sectionId = Integer.parseInt(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete selected section?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        boolean ok = adminService.deleteSection(sectionId);

        if (ok) {
            loadSectionsFromDb();
            JOptionPane.showMessageDialog(this, "Section deleted.");
        } else {
            JOptionPane.showMessageDialog(this, "Cannot delete. Students may be enrolled.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSectionsFromDb() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    List<Section> sections = adminService.getAllSections();
                    CourseDAO cdao = new CourseDAO();
                    List<Course> courses = cdao.getAll();

                    InstructorDAO idao = new InstructorDAO();
                    List<Instructor> instructors = idao.getAllInstructorProfiles();

                    SwingUtilities.invokeLater(() -> {
                        model.setRowCount(0);
                        for (Section s : sections) {
                            String courseCode = courses.stream()
                                    .filter(c -> c.getCourseid() == s.getCourseid())
                                    .map(Course::getCode)
                                    .findFirst().orElse("N/A");

                            String instructorName = instructors.stream()
                                    .filter(i -> i.getUserid() == s.getInstructorid())
                                    .map(Instructor::getName)
                                    .findFirst().orElse("—");

                            model.addRow(new Object[] {
                                    s.getSectionid(),
                                    courseCode,
                                    instructorName,
                                    s.getSemester(),
                                    s.getYear(),
                                    s.getCapacity()
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
}
