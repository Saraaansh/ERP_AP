package edu.univ.erp.ui.components;

import java.awt.*;

public class QuickActionButton extends RoundedButton {

    private Runnable action = () -> {
    };

    // Constructor WITHOUT action (default)
    public QuickActionButton(String text) {
        this(text, () -> {
        });
    }

    // Constructor WITH action
    public QuickActionButton(String text, Runnable action) {
        super(text, new Color(52, 152, 219)); // Modern Blue
        this.action = action;

        setPreferredSize(new Dimension(100, 50)); // Slightly taller for dashboard tiles

        addActionListener(e -> this.action.run());
    }
}
