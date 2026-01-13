package edu.univ.erp.ui.components;

import javax.swing.*;
import java.awt.*;

public class TitledContainer extends JPanel {

    private JPanel contentPanel;
    private JLabel titleLabel;

    public TitledContainer(String title) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Outer padding for shadow

        // Main container panel (will be painted)
        JPanel container = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);

                // Background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 15, 15);

                // Border
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 7, getHeight() - 7, 15, 15);

                g2.dispose();
            }
        };
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20)); // Inner padding

        // Title
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Content Area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        container.add(titleLabel, BorderLayout.NORTH);
        container.add(contentPanel, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);
    }

    public void setInnerComponent(Component c) {
        contentPanel.removeAll();
        contentPanel.add(c, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}
