package edu.univ.erp.ui.panels;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.util.ModernUI;

import javax.swing.*;
import java.awt.*;

/**
 * Student settings panel with profile, security, notifications, and appearance
 * options.
 */
public class SettingsPanel extends JPanel {

    private final String userName;
    private final MainFrame mainFrame;

    public SettingsPanel(String userName, MainFrame mainFrame) {
        this.userName = userName;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250));

        edu.univ.erp.ui.components.TitledContainer container = new edu.univ.erp.ui.components.TitledContainer(
                "Student Settings");
        container.setInnerComponent(buildContent());
        add(container, BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(buildProfileSection());
        content.add(Box.createVerticalStrut(20));
        content.add(new JSeparator());
        content.add(Box.createVerticalStrut(20));

        content.add(buildPasswordSection());
        content.add(Box.createVerticalStrut(20));
        content.add(new JSeparator());
        content.add(Box.createVerticalStrut(20));

        content.add(buildNotificationSection());
        content.add(Box.createVerticalStrut(20));
        content.add(new JSeparator());
        content.add(Box.createVerticalStrut(20));

        content.add(buildAppearanceSection());
        content.add(Box.createVerticalGlue());

        return content;
    }

    private JPanel buildProfileSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JLabel title = new JLabel("Profile");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setPreferredSize(new Dimension(200, 30));

        JLabel name = new JLabel("Logged in as: " + userName);
        name.setFont(new Font("SansSerif", Font.PLAIN, 14));

        panel.add(title);
        panel.add(name);

        return panel;
    }

    private JPanel buildPasswordSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JLabel title = new JLabel("Security");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setPreferredSize(new Dimension(200, 30));

        RoundedButton changePassBtn = new RoundedButton("Change Password", new Color(52, 152, 219));
        changePassBtn.setPreferredSize(new Dimension(160, 32));
        changePassBtn.addActionListener(e -> showChangePasswordDialog());

        panel.add(title);
        panel.add(changePassBtn);

        return panel;
    }

    /**
     * Shows the change password dialog and reopens it after validation errors.
     */
    private void showChangePasswordDialog() {
        if (!edu.univ.erp.service.MaintenanceService.checkMaintenance(this)) {
            return;
        }

        JPasswordField currentPassField = new JPasswordField();
        JPasswordField newPassField = new JPasswordField();
        JPasswordField confirmPassField = new JPasswordField();

        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.add(new JLabel("Current Password:"));
        p.add(currentPassField);
        p.add(new JLabel("New Password:"));
        p.add(newPassField);
        p.add(new JLabel("Confirm Password:"));
        p.add(confirmPassField);

        int result = JOptionPane.showConfirmDialog(this, p, "Change Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION)
            return;

        String currentPass = new String(currentPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(this::showChangePasswordDialog);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(this::showChangePasswordDialog);
            return;
        }

        if (newPass.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(this::showChangePasswordDialog);
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String error = null;

            @Override
            protected Boolean doInBackground() {
                try {
                    edu.univ.erp.service.AuthService authService = new edu.univ.erp.service.AuthService();
                    return authService.changePassword(currentPass, newPass);
                } catch (Exception ex) {
                    error = ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(SettingsPanel.this,
                                "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(SettingsPanel.this,
                                "Failed to change password: " + error, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SettingsPanel.this,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private JPanel buildNotificationSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JLabel title = new JLabel("Notifications");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setPreferredSize(new Dimension(200, 30));

        JCheckBox emailToggle = new JCheckBox("Enable Email Notifications");
        emailToggle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailToggle.setOpaque(false);

        panel.add(title);
        panel.add(emailToggle);

        return panel;
    }

    private JPanel buildAppearanceSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JLabel title = new JLabel("Appearance");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setPreferredSize(new Dimension(200, 30));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setOpaque(false);

        JLabel fontLbl = new JLabel("Font size:");
        fontLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JComboBox<String> fontSelect = new JComboBox<>(new String[] { "Small", "Medium", "Large" });
        fontSelect.setSelectedIndex(1);
        fontSelect.setPreferredSize(new Dimension(130, 30));
        fontSelect.addActionListener(e -> {
            if (!edu.univ.erp.service.MaintenanceService.checkMaintenance(SettingsPanel.this)) {
                return;
            }
            String selected = (String) fontSelect.getSelectedItem();
            ModernUI.updateFontSize(selected.toLowerCase());
        });

        controls.add(fontLbl);
        controls.add(fontSelect);

        panel.add(title);
        panel.add(controls);

        return panel;
    }
}
