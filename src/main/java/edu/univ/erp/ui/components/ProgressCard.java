package edu.univ.erp.ui.components;

import javax.swing.*;
import java.awt.*;

public class ProgressCard extends JPanel {

    private final JProgressBar progress;
    private final JLabel label;

    public ProgressCard(String title) {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);

        label = new JLabel("0 / 0 Credits");
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(titleLabel, BorderLayout.NORTH);
        add(progress, BorderLayout.CENTER);
        add(label, BorderLayout.SOUTH);
    }

    public void updateProgress(int percent, String text) {
        progress.setValue(percent);
        progress.setString(percent + "%");
        label.setText(text);
    }
}
