package edu.univ.erp.ui.panels.admin;

import edu.univ.erp.service.MaintenanceService;
import edu.univ.erp.domain.SystemSetting;

import javax.swing.*;
import java.awt.*;

public class MaintenanceSettingsPanel extends JPanel {

    private final MaintenanceService maintenanceService = new MaintenanceService();

    private JCheckBox maintenanceToggle;
    private JTextArea messageArea;

    public MaintenanceSettingsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        loadSettingsFromDb();
    }

    private JComponent buildHeader() {
        JLabel title = new JLabel("System Maintenance");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        return title;
    }

    private JComponent buildCenter() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        maintenanceToggle = new JCheckBox("Enable maintenance mode (students/instructors see banner)");
        maintenanceToggle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel msgLabel = new JLabel("Maintenance message shown to users:");
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msgLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        messageArea = new JTextArea(6, 40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(messageArea);

        panel.add(maintenanceToggle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(msgLabel);
        panel.add(scrollPane);

        // Align left
        maintenanceToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        msgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    private JComponent buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        JButton saveBtn = new JButton("Save Settings");
        saveBtn.addActionListener(e -> onSave());

        panel.add(saveBtn);
        return panel;
    }

    /**
     * -----------------------------------------------
     * LOAD SETTINGS FROM DB
     * -----------------------------------------------
     */
    private void loadSettingsFromDb() {
        SwingWorker<SystemSetting, Void> worker = new SwingWorker<>() {
            @Override
            protected SystemSetting doInBackground() throws Exception {
                return maintenanceService.getCurrentSettings();
            }

            @Override
            protected void done() {
                try {
                    SystemSetting setting = get();
                    if (setting != null) {
                        maintenanceToggle.setSelected(setting.isMaintenanceMode());
                        messageArea.setText(setting.getMessage());
                    }
                } catch (Exception ex) {
                    // Silent failure during initialization - leave fields with defaults
                    maintenanceToggle.setSelected(false);
                    messageArea.setText("");
                }
            }
        };
        worker.execute();
    }

    /**
     * -----------------------------------------------
     * SAVE SETTINGS TO DB
     * -----------------------------------------------
     */
    private void onSave() {
        boolean on = maintenanceToggle.isSelected();
        String msg = messageArea.getText().trim();

        try {
            boolean ok = maintenanceService.updateSettings(on, msg);

            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "Maintenance settings updated.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to update settings.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error while saving settings:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
