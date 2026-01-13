package edu.univ.erp.ui.panels.student;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.api.student.CatalogApi;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CourseCatalogPanel extends JPanel {
    private final StudentService studentService = new StudentService();
    private final CatalogApi catalogApi = new CatalogApi();

    private DefaultTableModel model;
    private JTable table;

    public CourseCatalogPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        TitledContainer container = new TitledContainer("Course Catalog - Available Sections");

        model = new DefaultTableModel(new Object[] {
                "Section ID", "Course Code", "Title", "Credits", "Instructor", "Day/Time", "Room", "Enrolled/Capacity"
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

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(236, 236, 236));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));

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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomPanel.setOpaque(false);

        RoundedButton registerBtn = new RoundedButton("Register", new Color(39, 174, 96));
        RoundedButton refreshBtn = new RoundedButton("Refresh", new Color(189, 195, 199));

        registerBtn.addActionListener(e -> registerForSection());
        refreshBtn.addActionListener(e -> loadCatalog(false));

        bottomPanel.add(refreshBtn);
        bottomPanel.add(registerBtn);

        container.setInnerComponent(scrollPane);
        add(container, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadCatalog(true);

        // Auto-refresh when this panel becomes visible
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadCatalog(true);
            }
        });
    }

    /**
     * Loads the course catalog sections from the database.
     * 
     * @param suppressPopup if true, does not show popup when no sections available
     */
    private void loadCatalog(boolean suppressPopup) {
        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            boolean hasError = false;

            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> sectionRows = new ArrayList<>();

                try {
                    List<CatalogApi.SectionRow> sections = catalogApi.listSections();
                    for (CatalogApi.SectionRow sr : sections) {
                        sectionRows.add(new Object[] {
                                sr.sectionId(),
                                sr.courseCode(),
                                sr.title(),
                                sr.credits(),
                                sr.instructor(),
                                sr.dayTime(),
                                sr.room(),
                                sr.enrolled() + "/" + sr.capacity()
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hasError = true;
                }

                return sectionRows;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> rows = get();
                    model.setRowCount(0);

                    if (rows != null) {
                        for (Object[] row : rows) {
                            model.addRow(row);
                        }
                    }

                    if ((rows == null || rows.isEmpty()) && !hasError && !suppressPopup) {
                        JOptionPane.showMessageDialog(CourseCatalogPanel.this,
                                "No sections are currently available for registration.",
                                "No Sections Available", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    /**
     * Registers the student for the selected section.
     */
    private void registerForSection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a section to register for.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!edu.univ.erp.service.MaintenanceService.checkMaintenance(this)) {
            return;
        }

        int sectionId = (int) model.getValueAt(selectedRow, 0);
        String courseCode = (String) model.getValueAt(selectedRow, 1);
        String courseTitle = (String) model.getValueAt(selectedRow, 2);
        String seatsInfo = (String) model.getValueAt(selectedRow, 7);

        if (seatsInfo != null && seatsInfo.contains("/")) {
            String[] parts = seatsInfo.split("/");
            try {
                int enrolled = Integer.parseInt(parts[0].trim());
                int capacity = Integer.parseInt(parts[1].trim());
                if (enrolled >= capacity) {
                    JOptionPane.showMessageDialog(this,
                            "Section is full (" + enrolled + "/" + capacity + " seats).",
                            "Section Full", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Register for " + courseCode + " - " + courseTitle + "?",
                "Confirm Registration",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String errorMsg = null;

            @Override
            protected Boolean doInBackground() {
                try {
                    int studentId = SessionManager.getCurrentUserId();

                    // The service handles duplicate checks and capacity
                    
                    return studentService.registerSection(studentId, sectionId);
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
                        JOptionPane.showMessageDialog(CourseCatalogPanel.this,
                                "Successfully registered for " + courseCode + "!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadCatalog(false);
                    } else {
                        JOptionPane.showMessageDialog(CourseCatalogPanel.this,
                                errorMsg != null ? errorMsg : "Registration failed.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CourseCatalogPanel.this,
                            "Registration failed: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
