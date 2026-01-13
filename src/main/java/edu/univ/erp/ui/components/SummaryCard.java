package edu.univ.erp.ui.components;

import javax.swing.*;
import java.awt.*;

public class SummaryCard extends JPanel {

    private Color cardColor;

    public SummaryCard(String title, JLabel valueLabel, Color backgroundColor) {
        this.cardColor = backgroundColor;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Outer padding for shadow

        // Main content panel
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(255, 255, 255, 220));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 32));

        content.add(titleLabel, BorderLayout.NORTH);
        content.add(valueLabel, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 20, 20);

        // Background
        g2.setColor(cardColor);
        g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);

        g2.dispose();
    }
}
