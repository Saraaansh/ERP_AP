package edu.univ.erp.ui.components;

import javax.swing.*;
import java.awt.*;

public class InfoCard extends JPanel {

    private JLabel titleLabel;
    private JLabel valueLabel;

    public InfoCard(String title, String value) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(new Color(245, 247, 250));

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(new Color(40, 70, 200));

        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(160, 80));
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
