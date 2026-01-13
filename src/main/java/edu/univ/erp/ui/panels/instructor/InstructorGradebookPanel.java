package edu.univ.erp.ui.panels.instructor;

import edu.univ.erp.domain.*;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;
import edu.univ.erp.ui.service.InstructorDashboardDataProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * InstructorGradebookPanel — shows assessments, allows editing scores and
 * computing final grades.
 *
 * NOTE: MainFrame expects panels to expose `loadSection(String sectionId,
 * String label)`.
 * This class parses the sectionId string and uses your existing DAOs +
 * InstructorService.
 */
public class InstructorGradebookPanel extends JPanel {
    private final InstructorService instructorService = new InstructorService();
    private final edu.univ.erp.service.StudentService studentService = new edu.univ.erp.service.StudentService();

    private final JLabel headerLabel = new JLabel("Gradebook - Select a section");
    private final DefaultTableModel gradeModel;
    private final JTable gradeTable;
    private final DefaultListModel<String> sectionListModel;
    private final JList<String> sectionList;
    private final Map<Integer, String> sectionIdToLabelMap = new HashMap<>();

    // keep enrollment id mapping: rowIndex -> enrollmentId
    private final Map<Integer, Integer> enrollmentIdMap = new HashMap<>();
    private List<Assessment> currentAssessments = Collections.emptyList();
    private int currentSectionInt = -1; // parsed section id

    private final InstructorDashboardDataProvider provider;

    public InstructorGradebookPanel(InstructorDashboardDataProvider provider) {
        this.provider = provider;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        // header
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerLabel.setForeground(new Color(44, 62, 80));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // LEFT PANEL: Section selection list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        JLabel selectLabel = new JLabel("Select Section:");
        selectLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        selectLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        sectionListModel = new DefaultListModel<>();
        sectionList = new JList<>(sectionListModel);
        sectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sectionList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sectionList.setBackground(Color.WHITE);
        sectionList.setFixedCellHeight(32);
        sectionList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        sectionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = sectionList.getSelectedIndex();
                if (idx >= 0) {
                    String selected = sectionListModel.get(idx);
                    // Extract section ID from list item (format: "ID - Course Code")
                    String[] parts = selected.split(" - ");
                    if (parts.length > 0) {
                        try {
                            int secId = Integer.parseInt(parts[0].trim());
                            String label = parts.length > 1 ? parts[1].trim() : "";
                            loadSection(String.valueOf(secId), label);
                        } catch (NumberFormatException ex) {
                            // ignore
                        }
                    }
                }
            }
        });

        leftPanel.add(selectLabel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(sectionList), BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // CENTER PANEL: Gradebook table
        // model: columns will be set when a section is loaded
        gradeModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // allow editing of assessment score columns only (not ID, name, final grade)
                // we'll decide this at runtime: first two columns fixed, last column final
                // grade non-editable
                int colCount = getColumnCount();
                if (colCount < 3)
                    return false;
                return column >= 2 && column < (colCount - 1);
            }
        };

        gradeTable = new JTable(gradeModel);
        gradeTable.setRowHeight(32);
        gradeTable.setShowVerticalLines(false);
        gradeTable.setIntercellSpacing(new Dimension(0, 0));

        // Header styling
        JTableHeader header = gradeTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(236, 236, 236));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));

        // Zebra striping
        gradeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        TitledContainer gbContainer = new TitledContainer("Scores and Final Grades");
        JScrollPane scroll = new JScrollPane(gradeTable);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        gbContainer.setInnerComponent(scroll);

        // action buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actions.setOpaque(false);

        RoundedButton refreshBtn = new RoundedButton("Refresh", new Color(52, 152, 219));
        refreshBtn.setPreferredSize(new Dimension(120, 36));

        RoundedButton saveGradesBtn = new RoundedButton("Save Grade Changes", new Color(46, 204, 113));
        saveGradesBtn.setPreferredSize(new Dimension(180, 36));

        RoundedButton computeFinalsBtn = new RoundedButton("Compute Final Grades", new Color(243, 156, 18));
        computeFinalsBtn.setPreferredSize(new Dimension(200, 36));

        RoundedButton exportBtn = new RoundedButton("Export to CSV", new Color(155, 89, 182));
        exportBtn.setPreferredSize(new Dimension(140, 36));

        refreshBtn.addActionListener(e -> {
            if (currentSectionInt > 0)
                loadSection(String.valueOf(currentSectionInt), "");
        });
        saveGradesBtn.addActionListener(e -> saveGradeChanges());
        computeFinalsBtn.addActionListener(e -> computeAllFinalGrades());
        exportBtn.addActionListener(e -> exportToCsv());

        actions.add(refreshBtn);
        actions.add(saveGradesBtn);
        actions.add(computeFinalsBtn);
        actions.add(exportBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(gbContainer, BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        // Load sections after UI is rendered
        SwingUtilities.invokeLater(this::loadAvailableSections);
    }

    /**
     * Load the list of sections for this instructor
     */
    private void loadAvailableSections() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<InstructorDashboardDataProvider.SectionRow> sections;

            @Override
            protected Void doInBackground() {
                try {
                    sections = provider.loadSections();
                } catch (Exception e) {
                    sections = Collections.emptyList();
                }
                return null;
            }

            @Override
            protected void done() {
                sectionListModel.clear();
                sectionIdToLabelMap.clear();

                if (sections != null && !sections.isEmpty()) {
                    for (InstructorDashboardDataProvider.SectionRow row : sections) {
                        String display = row.getSectionId() + " - " + row.getCourseCode();
                        sectionListModel.addElement(display);
                        sectionIdToLabelMap.put(Integer.parseInt(row.getSectionId()), row.getCourseCode());
                    }
                } else {
                    sectionListModel.addElement("No sections available");
                }
            }
        };
        worker.execute();
    }

    /**
     * MainFrame will call this when a section is selected.
     * Accepts `sectionId` as String to match MainFrame's contract.
     */
    public void loadSection(String sectionId, String label) {
        // parse id defensively
        int secId;
        try {
            secId = Integer.parseInt(sectionId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid section id: " + sectionId,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.currentSectionInt = secId;
        SwingUtilities.invokeLater(() -> headerLabel
                .setText("Gradebook – Section " + secId + (label != null && !label.isEmpty() ? " - " + label : "")));

        // load data on background thread
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<Enrollment> enrollments = Collections.emptyList();

            @Override
            protected Void doInBackground() {
                try {
                    currentAssessments = instructorService.getAssessments(secId);
                    enrollments = instructorService.getStudentsInSection(secId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // rethrow exceptions if any
                    buildTableModelAndPopulate(enrollments);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(InstructorGradebookPanel.this,
                            "Error loading gradebook: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Build table columns and populate rows using enrollments list
    private void buildTableModelAndPopulate(List<Enrollment> enrollments) {
        gradeModel.setRowCount(0);
        enrollmentIdMap.clear();

        int nAssess = currentAssessments == null ? 0 : currentAssessments.size();
        int cols = nAssess + 3; // ID, Name, <assessments...>, Final Grade
        String[] columnNames = new String[cols];
        columnNames[0] = "Student ID";
        columnNames[1] = "Student Name";
        for (int i = 0; i < nAssess; i++) {
            Assessment a = currentAssessments.get(i);
            String nm = a.getComponentName() != null ? a.getComponentName() : ("Comp" + (i + 1));
            columnNames[i + 2] = nm + " (" + a.getWeight() + "%) [0-100]";
        }
        columnNames[nAssess + 2] = "Final Grade";

        gradeModel.setColumnIdentifiers(columnNames);

        for (Enrollment enrollment : enrollments) {
            if (!"active".equalsIgnoreCase(enrollment.getStatus()))
                continue;

            Student student = studentService.getStudent(enrollment.getStudentid());
            if (student == null)
                continue;

            Object[] row = new Object[cols];
            row[0] = student.getUserid();
            row[1] = student.getName();

            // get grades for this enrollment
            List<Grade> grades;
            try {
                grades = instructorService.getGradesForEnrollment(enrollment.getEnrollmentid());
            } catch (Exception e) {
                grades = Collections.emptyList();
            }
            Map<String, Grade> gradeMap = new HashMap<>();
            for (Grade g : grades) {
                if (g.getComponent() != null)
                    gradeMap.put(g.getComponent().toUpperCase(), g);
            }

            for (int i = 0; i < nAssess; i++) {
                String component = currentAssessments.get(i).getComponentName();
                Grade g = component == null ? null : gradeMap.get(component.toUpperCase());
                row[i + 2] = (g != null) ? String.format("%.1f", g.getScore()) : "";
            }

            Grade finalG = gradeMap.get("FINAL_GRADE");
            row[nAssess + 2] = (finalG != null && finalG.getFinalgrade() != null) ? finalG.getFinalgrade() : "-";

            int rowIndex = gradeModel.getRowCount(); // index where row will be inserted
            gradeModel.addRow(row);
            enrollmentIdMap.put(rowIndex, enrollment.getEnrollmentid());
        }

    }

    // Save edited scores back to DB via InstructorService
    private void saveGradeChanges() {
        if (currentSectionInt <= 0 || currentAssessments.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No section loaded or no assessments defined.",
                    "Cannot Save", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Maintenance check
        if (!edu.univ.erp.service.MaintenanceService.checkMaintenance(this)) {
            return;
        }

        if (gradeTable.isEditing()) {
            gradeTable.getCellEditor().stopCellEditing();
        }

        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            int savedCount = 0;
            String error = null;

            @Override
            protected Integer doInBackground() {
                try {
                    for (int row = 0; row < gradeModel.getRowCount(); row++) {
                        Integer enrollmentId = enrollmentIdMap.get(row);
                        if (enrollmentId == null)
                            continue;

                        for (int i = 0; i < currentAssessments.size(); i++) {
                            Object scoreObj = gradeModel.getValueAt(row, i + 2);
                            if (scoreObj == null)
                                continue;
                            String txt = scoreObj.toString().trim();
                            if (txt.isEmpty())
                                continue;

                            double score;
                            try {
                                score = Double.parseDouble(txt);
                            } catch (NumberFormatException nf) {
                                // skip invalid numeric values
                                continue;
                            }

                            String component = currentAssessments.get(i).getComponentName();
                            // Find existing grade for enrollment+component
                            List<Grade> existing = instructorService.getGradesForEnrollment(enrollmentId);
                            Grade found = null;
                            for (Grade g : existing) {
                                if (g.getComponent() != null && g.getComponent().equalsIgnoreCase(component)) {
                                    found = g;
                                    break;
                                }
                            }

                            if (found != null) {
                                instructorService.updateGrade(found.getGradeid(), component, score, null);
                            } else {
                                instructorService.addGrade(enrollmentId, component, score);
                            }
                            savedCount++;
                        }
                    }
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                return savedCount;
            }

            @Override
            protected void done() {
                try {
                    int count = get();
                    if (error != null) {
                        JOptionPane.showMessageDialog(InstructorGradebookPanel.this,
                                "Error saving grades: " + error,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(InstructorGradebookPanel.this,
                                "Saved " + count + " grade entries.",
                                "Saved", JOptionPane.INFORMATION_MESSAGE);
                        // refresh view
                        loadSection(String.valueOf(currentSectionInt), "");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(InstructorGradebookPanel.this,
                            "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Compute final grades for all enrollments in current section
    private void computeAllFinalGrades() {
        if (currentSectionInt <= 0 || currentAssessments.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No section loaded or no assessments defined.",
                    "Cannot Compute", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Maintenance check
        if (!edu.univ.erp.service.MaintenanceService.checkMaintenance(this)) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Compute final grades for all students in this section?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            int computed = 0;
            String error = null;

            @Override
            protected Integer doInBackground() {
                try {
                    for (Integer enrollmentId : enrollmentIdMap.values()) {
                        try {
                            instructorService.computeFinalGrade(enrollmentId);
                            computed++;
                        } catch (Exception e) {
                            // continue other students
                        }
                    }
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                return computed;
            }

            @Override
            protected void done() {
                try {
                    int c = get();
                    if (error != null) {
                        JOptionPane.showMessageDialog(InstructorGradebookPanel.this,
                                "Error computing finals: " + error,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(InstructorGradebookPanel.this,
                                "Computed final grades for " + c + " students.",
                                "Done", JOptionPane.INFORMATION_MESSAGE);
                        loadSection(String.valueOf(currentSectionInt), "");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(InstructorGradebookPanel.this,
                            "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Export displayed table to CSV by delegating to InstructorService
    private void exportToCsv() {
        if (currentSectionInt <= 0) {
            JOptionPane.showMessageDialog(this,
                    "No section loaded.",
                    "Cannot Export", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("grades_section_" + currentSectionInt + ".csv"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION)
            return;

        String path = chooser.getSelectedFile().getAbsolutePath();

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String error = null;

            @Override
            protected Boolean doInBackground() {
                try {
                    instructorService.exportGradesCsv(currentSectionInt, path);
                    return true;
                } catch (Exception ex) {
                    error = ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(InstructorGradebookPanel.this,
                                "Exported to: " + path,
                                "Exported", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(InstructorGradebookPanel.this,
                                "Export failed: " + error,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(InstructorGradebookPanel.this,
                            "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
