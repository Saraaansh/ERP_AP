package edu.univ.erp.ui.panels.admin;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.User;
import edu.univ.erp.service.AdminService;

import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> roleFilter;
    private JTextField searchField;

    public UserManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        loadUsers();
    }

    /*
     * -------------------------------------------------------------
     * HEADER (Filters + Search)
     * -------------------------------------------------------------
     */
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("User Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        roleFilter = new JComboBox<>(new String[] { "All", "STUDENT", "INSTRUCTOR", "ADMIN" });
        roleFilter.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleFilter.setBackground(Color.WHITE);

        searchField = new JTextField(16);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        RoundedButton searchBtn = new RoundedButton("Search", new Color(52, 152, 219));
        searchBtn.setPreferredSize(new Dimension(100, 35));
        searchBtn.addActionListener(e -> loadUsers());

        JLabel roleLbl = new JLabel("Role:");
        roleLbl.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(new Font("SansSerif", Font.BOLD, 14));

        filters.add(roleLbl);
        filters.add(roleFilter);
        filters.add(searchLbl);
        filters.add(searchField);
        filters.add(searchBtn);

        header.add(title, BorderLayout.WEST);
        header.add(filters, BorderLayout.EAST);

        return header;
    }

    /*
     * -------------------------------------------------------------
     * TABLE
     * -------------------------------------------------------------
     */
    private JComponent buildTableArea() {
        TitledContainer container = new TitledContainer("User List");

        model = new DefaultTableModel(
                new Object[] { "User ID", "Username", "Role" }, 0) {
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        container.setInnerComponent(scrollPane);
        return container;
    }

    /*
     * -------------------------------------------------------------
     * ACTION BUTTONS (Add / Edit / Delete)
     * -------------------------------------------------------------
     */
    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        p.setOpaque(false);

        RoundedButton addBtn = new RoundedButton("Add User", new Color(46, 204, 113));
        RoundedButton editBtn = new RoundedButton("Edit Selected", new Color(52, 152, 219));
        RoundedButton deleteBtn = new RoundedButton("Delete Selected", new Color(231, 76, 60));

        addBtn.addActionListener(e -> onAddUser());
        editBtn.addActionListener(e -> onEditUser());
        deleteBtn.addActionListener(e -> onDeleteUser());

        p.add(addBtn);
        p.add(editBtn);
        p.add(deleteBtn);

        return p;
    }

    /*
     * -------------------------------------------------------------
     * ADD USER
     * -------------------------------------------------------------
     */
    private void onAddUser() {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JComboBox<String> roleField = new JComboBox<>(new String[] { "STUDENT", "INSTRUCTOR", "ADMIN" });

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Username:"));
        p.add(usernameField);

        p.add(new JLabel("Password:"));
        p.add(passwordField);

        p.add(new JLabel("Role:"));
        p.add(roleField);

        int result = JOptionPane.showConfirmDialog(this, p, "Create User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION)
            return;

        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleField.getSelectedItem().toString();

            // Validation
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username is required.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password is required.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (password.length() < 4) {
                JOptionPane.showMessageDialog(this, "Password must be at least 4 characters.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean ok;

            if (role.equals("STUDENT")) {
                Student st = new Student();
                st.setName(username);
                // st.setEmail(username + "@univ.edu");
                ok = adminService.createStudentProfile(username, password, st);
            } else if (role.equals("INSTRUCTOR")) {
                Instructor ins = new Instructor();
                ins.setName(username);
                ins.setEmail(username + "@univ.edu");
                ok = adminService.createInstructorProfile(username, password, ins);
            } else { // ADMIN only creates auth user
                ok = adminService.createAdminUser(username, password);
            }

            if (ok) {
                JOptionPane.showMessageDialog(this, "User created successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create user.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * -------------------------------------------------------------
     * EDIT USER
     * -------------------------------------------------------------
     */
    private void onEditUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);
        String role = (String) model.getValueAt(row, 2);

        JTextField usernameField = new JTextField(username);
        JComboBox<String> roleField = new JComboBox<>(new String[] { "STUDENT", "INSTRUCTOR", "ADMIN" });
        roleField.setSelectedItem(role);

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Username:"));
        p.add(usernameField);
        p.add(new JLabel("Role:"));
        p.add(roleField);

        int result = JOptionPane.showConfirmDialog(this, p, "Edit User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION)
            return;

        try {
            String newUsername = usernameField.getText().trim();
            String newRole = roleField.getSelectedItem().toString();

            // Validation
            if (newUsername.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username is required.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean ok = adminService.updateUser(userId, newUsername, newRole);

            if (ok) {
                JOptionPane.showMessageDialog(this, "User updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * -------------------------------------------------------------
     * DELETE USER
     * -------------------------------------------------------------
     */
    private void onDeleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user first.");
            return;
        }

        int userId = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        try {
            boolean ok = adminService.deleteUser(userId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "User deleted.");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * -------------------------------------------------------------
     * LOAD USERS (Search + Filter)
     * -------------------------------------------------------------
     */
    private void loadUsers() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    String filter = roleFilter.getSelectedItem().toString();
                    String search = searchField.getText().trim();

                    List<User> users = adminService.searchUsers(filter, search);

                    SwingUtilities.invokeLater(() -> {
                        model.setRowCount(0);
                        for (User u : users) {
                            model.addRow(new Object[] {
                                    u.getUserId(),
                                    u.getUsername(),
                                    u.getRole()
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
