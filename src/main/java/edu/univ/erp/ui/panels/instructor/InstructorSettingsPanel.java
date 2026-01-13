package edu.univ.erp.ui.panels.instructor;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;
import edu.univ.erp.ui.util.ModernUI;

import javax.swing.*;
import java.awt.*;

/**
 * Instructor settings panel with security, notifications, and dashboard
 * preferences.
 */
public class InstructorSettingsPanel extends JPanel {

    private MainFrame mainFrame;
    private boolean alertPending = true;
    private boolean alertAnnouncements = true;
    private boolean showStudentCount = true;
    private boolean showPendingTasks = true;
    private int autoRefreshMinutes = 5;

    public InstructorSettingsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250));

        TitledContainer container = new TitledContainer("Instructor Settings");

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

        inner.add(buildNotificationSection());
        inner.add(Box.createVerticalStrut(20));
        inner.add(new JSeparator());
        inner.add(Box.createVerticalStrut(20));

        inner.add(buildDashboardSection());
        inner.add(Box.createVerticalStrut(20));
        inner.add(new JSeparator());
        inner.add(Box.createVerticalStrut(20));

        inner.add(buildAutoRefreshSection());
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
            if (!edu.univ.erp.service.MaintenanceService.checkMaintenance(this)) {
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

    private JPanel buildNotificationSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = sectionTitle("Notifications");
        title.setAlignmentX(LEFT_ALIGNMENT);

        JCheckBox chkPending = new JCheckBox("Alert when grading pending");
        JCheckBox chkAnnounce = new JCheckBox("Show system announcements");
        chkPending.setOpaque(false);
        chkAnnounce.setOpaque(false);
        chkPending.setAlignmentX(LEFT_ALIGNMENT);
        chkAnnounce.setAlignmentX(LEFT_ALIGNMENT);

        chkPending.setSelected(alertPending);
        chkAnnounce.setSelected(alertAnnouncements);

        chkPending.addActionListener(e -> alertPending = chkPending.isSelected());
        chkAnnounce.addActionListener(e -> alertAnnouncements = chkAnnounce.isSelected());

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(chkPending);
        panel.add(chkAnnounce);

        return panel;
    }

    private JPanel buildDashboardSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = sectionTitle("Dashboard");
        title.setAlignmentX(LEFT_ALIGNMENT);

        JCheckBox chkCount = new JCheckBox("Show total student count");
        JCheckBox chkTasks = new JCheckBox("Show pending tasks");
        chkCount.setOpaque(false);
        chkTasks.setOpaque(false);
        chkCount.setAlignmentX(LEFT_ALIGNMENT);
        chkTasks.setAlignmentX(LEFT_ALIGNMENT);

        chkCount.setSelected(showStudentCount);
        chkTasks.setSelected(showPendingTasks);

        chkCount.addActionListener(e -> showStudentCount = chkCount.isSelected());
        chkTasks.addActionListener(e -> showPendingTasks = chkTasks.isSelected());

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(chkCount);
        panel.add(chkTasks);

        return panel;
    }

    private JPanel buildAutoRefreshSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JLabel title = sectionTitle("Auto-Refresh");
        title.setPreferredSize(new Dimension(200, 30));

        JSpinner refreshSpinner = new JSpinner(new SpinnerNumberModel(autoRefreshMinutes, 1, 60, 1));
        refreshSpinner.setPreferredSize(new Dimension(100, 30));
        refreshSpinner.addChangeListener(e -> autoRefreshMinutes = (int) refreshSpinner.getValue());

        JLabel unit = new JLabel("minutes");
        unit.setFont(new Font("SansSerif", Font.PLAIN, 14));

        panel.add(title);
        panel.add(refreshSpinner);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(unit);

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
                        JOptionPane.showMessageDialog(InstructorSettingsPanel.this,
                                "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(InstructorSettingsPanel.this,
                                "Failed to change password: " + error, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(InstructorSettingsPanel.this,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
