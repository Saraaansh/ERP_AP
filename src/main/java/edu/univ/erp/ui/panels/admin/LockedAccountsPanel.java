package edu.univ.erp.ui.panels.admin;

import edu.univ.erp.data.UserDAO;
import edu.univ.erp.domain.User;

import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LockedAccountsPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();
    private DefaultTableModel model;
    private JTable table;

    public LockedAccountsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        loadUsers();
    }

    private JComponent buildHeader() {
        JLabel title = new JLabel("Locked / Blocked Accounts");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return title;
    }

    private JComponent buildTableArea() {
        TitledContainer container = new TitledContainer("Account Status");

        model = new DefaultTableModel(
                new Object[] { "User ID", "Username", "Role", "Status", "Failed Attempts", "Lock Time", "Lock Reason" },
                0) {
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

        RoundedButton refreshBtn = new RoundedButton("Refresh", new Color(52, 152, 219));
        refreshBtn.setPreferredSize(new Dimension(120, 36));

        RoundedButton blockBtn = new RoundedButton("Block Selected User", new Color(231, 76, 60));
        blockBtn.setPreferredSize(new Dimension(180, 36));

        RoundedButton unblockBtn = new RoundedButton("Unblock Selected User", new Color(46, 204, 113));
        unblockBtn.setPreferredSize(new Dimension(190, 36));

        refreshBtn.addActionListener(e -> loadUsers());
        blockBtn.addActionListener(e -> onBlockUser());
        unblockBtn.addActionListener(e -> onUnblockUser());

        panel.add(refreshBtn);
        panel.add(blockBtn);
        panel.add(unblockBtn);

        return panel;
    }

    private void loadUsers() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    List<User> users = userDAO.getAllUsersWithLockStatus();

                    SwingUtilities.invokeLater(() -> {
                        model.setRowCount(0);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                        for (User user : users) {
                            String lockTimeStr = "-";
                            String lockReason = "-";

                            LocalDateTime lockTime = user.getLockTime();
                            if (lockTime != null) {
                                LocalDateTime now = LocalDateTime.now();
                                if (lockTime.isAfter(now)) {
                                    lockTimeStr = lockTime.format(formatter);
                                    lockReason = "5 Failed Attempts";
                                } else {
                                    lockTimeStr = "Expired";
                                    lockReason = "Auto-lock expired";
                                }
                            }

                            // Check if manually blocked (DISABLED status)
                            if ("DISABLED".equalsIgnoreCase(user.getStatus())) {
                                lockReason = "Admin Block";
                            }

                            model.addRow(new Object[] {
                                    user.getUserId(),
                                    user.getUsername(),
                                    user.getRole(),
                                    user.getStatus(),
                                    user.getFailedAttempts(),
                                    lockTimeStr,
                                    lockReason
                            });
                        }
                    });
                } catch (Exception e) {
                    // Silent failure during initialization
                    SwingUtilities.invokeLater(() -> model.setRowCount(0));
                }
                return null;
            }
        };
        worker.execute();
    }

    private void onBlockUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to block.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);
        String currentStatus = (String) model.getValueAt(row, 3);

        if ("DISABLED".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "User is already blocked.", "Already Blocked",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Block user '" + username + "'?\nThey will not be able to log in.",
                "Confirm Block", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        boolean success = userDAO.manuallyBlockUser(userId);

        if (success) {
            JOptionPane.showMessageDialog(this, "User blocked successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to block user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUnblockUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to unblock.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Unblock user '" + username + "'?\nThey will be able to log in again.",
                "Confirm Unblock", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        boolean success = userDAO.manuallyUnblockUser(userId);

        if (success) {
            JOptionPane.showMessageDialog(this, "User unblocked successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to unblock user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
