package edu.univ.erp.ui.panels.admin;

import edu.univ.erp.domain.Term;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TermManagementPanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private DefaultTableModel model;
    private JTable table;

    public TermManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        loadTerms();
    }

    private JComponent buildHeader() {
        JLabel title = new JLabel("Term Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return title;
    }

    private JComponent buildTableArea() {
        TitledContainer container = new TitledContainer("Academic Terms");

        model = new DefaultTableModel(
                new Object[] { "ID", "Name", "Start Date", "End Date", "Drop Deadline" }, 0) {
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

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        container.setInnerComponent(scroll);
        return container;
    }

    private JComponent buildActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setOpaque(false);

        RoundedButton addBtn = new RoundedButton("Add Term", new Color(46, 204, 113));
        RoundedButton editBtn = new RoundedButton("Edit Selected", new Color(52, 152, 219));
        RoundedButton deleteBtn = new RoundedButton("Delete Selected", new Color(231, 76, 60));

        addBtn.addActionListener(e -> onAddTerm());
        editBtn.addActionListener(e -> onEditTerm());
        deleteBtn.addActionListener(e -> onDeleteTerm());

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        return panel;
    }

    private void loadTerms() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    List<Term> list = adminService.getAllTerms();
                    SwingUtilities.invokeLater(() -> {
                        model.setRowCount(0);
                        for (Term t : list) {
                            model.addRow(new Object[] {
                                    t.getTermId(),
                                    t.getName(),
                                    t.getStartDate(),
                                    t.getEndDate(),
                                    t.getDropDeadline()
                            });
                        }
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> model.setRowCount(0));
                }
                return null;
            }
        };
        worker.execute();
    }

    private void onAddTerm() {
        JTextField nameField = new JTextField();
        JTextField startField = new JTextField("YYYY-MM-DD");
        JTextField endField = new JTextField("YYYY-MM-DD");
        JTextField dropField = new JTextField("YYYY-MM-DD");

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Name (e.g. Fall 2024):"));
        p.add(nameField);
        p.add(new JLabel("Start Date:"));
        p.add(startField);
        p.add(new JLabel("End Date:"));
        p.add(endField);
        p.add(new JLabel("Drop Deadline:"));
        p.add(dropField);

        int result = JOptionPane.showConfirmDialog(this, p, "Add Term", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                LocalDate start = LocalDate.parse(startField.getText().trim());
                LocalDate end = LocalDate.parse(endField.getText().trim());
                LocalDate drop = LocalDate.parse(dropField.getText().trim());

                if (name.isEmpty())
                    throw new Exception("Name required");
                if (end.isBefore(start))
                    throw new Exception("End date cannot be before start date");

                Term t = new Term(0, name, start, end, drop);
                if (adminService.createTerm(t)) {
                    JOptionPane.showMessageDialog(this, "Term created.");
                    loadTerms();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create term.");
                }

            } catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void onEditTerm() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a term first.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String oldName = (String) model.getValueAt(row, 1);
        LocalDate oldStart = (LocalDate) model.getValueAt(row, 2);
        LocalDate oldEnd = (LocalDate) model.getValueAt(row, 3);
        LocalDate oldDrop = (LocalDate) model.getValueAt(row, 4);

        JTextField nameField = new JTextField(oldName);
        JTextField startField = new JTextField(oldStart.toString());
        JTextField endField = new JTextField(oldEnd.toString());
        JTextField dropField = new JTextField(oldDrop.toString());

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Name:"));
        p.add(nameField);
        p.add(new JLabel("Start Date:"));
        p.add(startField);
        p.add(new JLabel("End Date:"));
        p.add(endField);
        p.add(new JLabel("Drop Deadline:"));
        p.add(dropField);

        int result = JOptionPane.showConfirmDialog(this, p, "Edit Term", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                LocalDate start = LocalDate.parse(startField.getText().trim());
                LocalDate end = LocalDate.parse(endField.getText().trim());
                LocalDate drop = LocalDate.parse(dropField.getText().trim());

                if (name.isEmpty())
                    throw new Exception("Name required");
                if (end.isBefore(start))
                    throw new Exception("End date cannot be before start date");

                Term t = new Term(id, name, start, end, drop);
                if (adminService.updateTerm(t)) {
                    JOptionPane.showMessageDialog(this, "Term updated.");
                    loadTerms();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update term.");
                }

            } catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void onDeleteTerm() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a term first.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this term?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (adminService.deleteTerm(id)) {
                JOptionPane.showMessageDialog(this, "Term deleted.");
                loadTerms();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete term.");
            }
        }
    }
}
