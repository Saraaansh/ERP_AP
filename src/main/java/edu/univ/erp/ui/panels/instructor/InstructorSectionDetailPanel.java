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
import java.util.List;

public class InstructorSectionDetailPanel extends JPanel {

    private final InstructorService instructorService = new InstructorService();

    private JLabel headerLabel;
    private JTable assessmentTable;
    private DefaultTableModel assessmentModel;

    // must be int internally but MainFrame gives string
    private int currentSectionId = -1;

    public InstructorSectionDetailPanel(InstructorDashboardDataProvider provider) {

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background
        buildUI();
    }

    private void buildUI() {

        headerLabel = new JLabel("Section Details – Assessment Management");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerLabel.setForeground(new Color(44, 62, 80));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        assessmentModel = new DefaultTableModel(
                new Object[] { "Assessment ID", "Component Name", "Weight (%)" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false; // no inline edits
            }
        };

        assessmentTable = new JTable(assessmentModel);
        assessmentTable.setRowHeight(32);
        assessmentTable.setShowVerticalLines(false);
        assessmentTable.setIntercellSpacing(new Dimension(0, 0));

        // Header styling
        JTableHeader header = assessmentTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(236, 236, 236));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));

        // Zebra striping
        assessmentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        TitledContainer c = new TitledContainer("Assessments");
        JScrollPane scroll = new JScrollPane(assessmentTable);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        c.setInnerComponent(scroll);

        RoundedButton addBtn = new RoundedButton("Add", new Color(46, 204, 113));
        RoundedButton deleteBtn = new RoundedButton("Delete", new Color(231, 76, 60));
        RoundedButton refreshBtn = new RoundedButton("Refresh", new Color(52, 152, 219));

        addBtn.addActionListener(e -> addAssessment());
        deleteBtn.addActionListener(e -> deleteAssessment());
        refreshBtn.addActionListener(e -> {
            if (currentSectionId > 0)
                loadSection(String.valueOf(currentSectionId), "");
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actions.setOpaque(false);
        actions.add(refreshBtn);
        actions.add(addBtn);
        actions.add(deleteBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(c, BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
    }

    // ✔ MainFrame calls this version
    public void loadSection(String sectionId, String label) {
        try {
            currentSectionId = Integer.parseInt(sectionId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid section ID: " + sectionId,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        headerLabel.setText(
                "Section " + sectionId + " – Assessment Management" +
                        (label == null || label.isEmpty() ? "" : " (" + label + ")"));

        SwingWorker<List<Assessment>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Assessment> doInBackground() throws Exception {
                return instructorService.getAssessments(currentSectionId);
            }

            @Override
            protected void done() {
                try {
                    List<Assessment> assessments = get();
                    assessmentModel.setRowCount(0);

                    int totalWeight = 0;

                    for (Assessment a : assessments) {
                        assessmentModel.addRow(new Object[] {
                                a.getAssessmentId(),
                                a.getComponentName(),
                                a.getWeight()
                        });
                        totalWeight += a.getWeight();
                    }

                    if (assessments.isEmpty()) {
                        assessmentModel.addRow(new Object[] { "No assessments", "", "" });
                    } else if (totalWeight != 100) {
                        JOptionPane.showMessageDialog(
                                InstructorSectionDetailPanel.this,
                                "Warning: Total weight = " + totalWeight + "% (should be 100%)",
                                "Weight Warning",
                                JOptionPane.WARNING_MESSAGE);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            InstructorSectionDetailPanel.this,
                            "Error loading assessments: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void addAssessment() {
        if (currentSectionId <= 0) {
            JOptionPane.showMessageDialog(this,
                    "No section selected.",
                    "Cannot Add", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Maintenance check
        if (!edu.univ.erp.service.MaintenanceService.checkMaintenance(this)) {
            return;
        }

        JTextField nameField = new JTextField();
        JTextField weightField = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Component Name:"));
        p.add(nameField);
        p.add(new JLabel("Weight (%):"));
        p.add(weightField);

        int result = JOptionPane.showConfirmDialog(
                this, p, "Add Assessment",
                JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION)
            return;

        String name = nameField.getText().trim();
        String weightStr = weightField.getText().trim();

        if (name.isEmpty() || weightStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields required.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int w;
        try {
            w = Integer.parseInt(weightStr);
            if (w <= 0 || w > 100)
                throw new NumberFormatException();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Weight must be a valid number (1–100).",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String error;

            @Override
            protected Boolean doInBackground() {
                try {
                    return instructorService.createAssessment(currentSectionId, name, w);
                } catch (Exception ex) {
                    error = ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(
                                InstructorSectionDetailPanel.this,
                                "Assessment added.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadSection(String.valueOf(currentSectionId), "");
                    } else {
                        JOptionPane.showMessageDialog(
                                InstructorSectionDetailPanel.this,
                                "Failed: " + error,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            InstructorSectionDetailPanel.this,
                            "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void deleteAssessment() {
        int row = assessmentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select an assessment first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Maintenance check
        if (!edu.univ.erp.service.MaintenanceService.checkMaintenance(this)) {
            return;
        }

        Object idObj = assessmentModel.getValueAt(row, 0);
        if (!(idObj instanceof Integer)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid row.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) idObj;
        String name = (String) assessmentModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete assessment \"" + name + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    return instructorService.deleteAssessment(id);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(
                                InstructorSectionDetailPanel.this,
                                "Assessment deleted.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadSection(String.valueOf(currentSectionId), "");
                    } else {
                        JOptionPane.showMessageDialog(
                                InstructorSectionDetailPanel.this,
                                "Delete failed.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            InstructorSectionDetailPanel.this,
                            "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }
}
