package edu.univ.erp.ui.panels.admin;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;
import edu.univ.erp.ui.util.ModernUI;

import javax.swing.*;
import java.awt.*;

/**
 * Admin settings panel with security and system information.
 */
public class AdminSettingsPanel extends JPanel {

    private MainFrame mainFrame;

    public AdminSettingsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250));

        TitledContainer container = new TitledContainer("Admin Settings");

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        inner.add(buildSecuritySection());
        inner.add(Box.createVerticalStrut(20));
        inner.add(new JSeparator());
        inner.add(Box.createVerticalStrut(20));

        inner.add(buildAppearanceSection());
        inner.add(Box.createVerticalStrut(20));
        inner.add(new JSeparator());
        inner.add(Box.createVerticalStrut(20));

        inner.add(buildSystemInfoSection());
        inner.add(Box.createVerticalGlue());

        container.setInnerComponent(inner);
        add(container, BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Container parent = getTopLevelAncestor();
        if (parent instanceof MainFrame mf) {
            this.mainFrame = mf;
        }
    }

    private JPanel buildSecuritySection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JLabel title = sectionTitle("Security");
        title.setPreferredSize(new Dimension(200, 30));

        RoundedButton changePasswordBtn = new RoundedButton("Change Password", new Color(52, 152, 219));
        changePasswordBtn.setPreferredSize(new Dimension(160, 32));
        changePasswordBtn.addActionListener(e -> showChangePasswordDialog());

        panel.add(title);
        panel.add(changePasswordBtn);

        return panel;
    }

    private JPanel buildAppearanceSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JLabel title = sectionTitle("Appearance");
        title.setPreferredSize(new Dimension(200, 30));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setOpaque(false);

        JLabel fontLbl = new JLabel("Font size:");
        fontLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JComboBox<String> fontSelect = new JComboBox<>(new String[] { "Small", "Medium", "Large" });
        fontSelect.setSelectedIndex(1);
        fontSelect.setPreferredSize(new Dimension(130, 30));
        fontSelect.addActionListener(e -> {
            String selected = (String) fontSelect.getSelectedItem();
            ModernUI.updateFontSize(selected.toLowerCase());
        });

        controls.add(fontLbl);
        controls.add(fontSelect);

        panel.add(title);
        panel.add(controls);

        return panel;
    }

    private JPanel buildSystemInfoSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = sectionTitle("System Information");
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel versionLabel = new JLabel("ERP System v1.0");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        versionLabel.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(versionLabel);

        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Shows the change password dialog and reopens it after validation errors.
     */
    private void showChangePasswordDialog() {
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
                        JOptionPane.showMessageDialog(AdminSettingsPanel.this,
                                "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(AdminSettingsPanel.this,
                                "Failed to change password: " + error, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminSettingsPanel.this,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
