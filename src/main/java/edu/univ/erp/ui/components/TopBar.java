package edu.univ.erp.ui.components;

import edu.univ.erp.auth.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Modern top bar with circular profile icon and theme-aware styling.
 */
public class TopBar extends JPanel {

    private JPanel profilePanel;
    private JLabel usernameLabel;
    private CircularIconLabel iconLabel;
    private JPanel maintenanceBanner;

    public TopBar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Remove padding to let banner flush
        setBackground(new Color(245, 247, 250));

        // Maintenance Banner (Hidden by default)
        maintenanceBanner = new JPanel(new FlowLayout(FlowLayout.CENTER));
        maintenanceBanner.setBackground(new Color(231, 76, 60));
        JLabel maintLabel = new JLabel("⚠️ SYSTEM MAINTENANCE MODE IS ACTIVE");
        maintLabel.setForeground(Color.WHITE);
        maintLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        maintenanceBanner.add(maintLabel);
        maintenanceBanner.setVisible(false);

        add(maintenanceBanner, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 20));

        profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        profilePanel.setOpaque(false);

        String username = "User";
        try {
            if (SessionManager.isLoggedIn()) {
                username = SessionManager.getCurrentUsername();
                if (username == null || username.isEmpty()) {
                    username = "User";
                }
            }
        } catch (Exception e) {
            username = "User";
        }

        iconLabel = new CircularIconLabel(getInitial(username));

        usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(44, 62, 80));

        profilePanel.add(iconLabel);
        profilePanel.add(usernameLabel);

        content.add(profilePanel, BorderLayout.EAST);
        add(content, BorderLayout.CENTER);
    }

    public void setMaintenanceMode(boolean active, String message) {
        maintenanceBanner.setVisible(active);
        if (active && message != null && !message.isEmpty()) {
            ((JLabel) maintenanceBanner.getComponent(0)).setText("⚠️ MAINTENANCE: " + message);
        } else {
            ((JLabel) maintenanceBanner.getComponent(0)).setText("⚠️ SYSTEM MAINTENANCE MODE IS ACTIVE");
        }
        revalidate();
        repaint();
    }

    /**
     * Sets the username displayed in the top bar.
     */
    public void setUsername(String name) {
        if (name != null && !name.isEmpty()) {
            usernameLabel.setText(name);
            iconLabel.setInitial(getInitial(name));
        }
    }

    /**
     * Updates the top bar colors for theme switching.
     */
    public void updateTheme(boolean isDark) {
        setBackground(isDark ? new Color(30, 30, 30) : new Color(245, 247, 250));
        usernameLabel.setForeground(isDark ? Color.WHITE : new Color(44, 62, 80));
        iconLabel.updateTheme(isDark);
        repaint();
    }

    private String getInitial(String username) {
        if (username == null || username.isEmpty()) {
            return "U";
        }
        return username.substring(0, 1).toUpperCase();
    }

    /**
     * Circular profile icon with initial letter.
     */
    private static class CircularIconLabel extends JLabel {
        private String initial;
        private boolean isDarkTheme = false;

        public CircularIconLabel(String initial) {
            this.initial = initial;
            setPreferredSize(new Dimension(28, 28));
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
            setForeground(Color.WHITE);
        }

        public void setInitial(String initial) {
            this.initial = initial;
            repaint();
        }

        public void updateTheme(boolean isDark) {
            this.isDarkTheme = isDark;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillOval(1, 2, 27, 27);

            g2.setColor(isDarkTheme ? new Color(70, 130, 255) : new Color(52, 152, 219));
            g2.fillOval(0, 0, 27, 27);

            g2.setColor(new Color(208, 208, 208));
            g2.drawOval(0, 0, 27, 27);

            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(initial)) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(initial, x, y);

            g2.dispose();
        }
    }
}
