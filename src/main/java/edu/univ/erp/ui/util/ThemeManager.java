package edu.univ.erp.ui.util;

import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;

import javax.swing.*;

public class ThemeManager {

    private static String currentTheme = "light";

    public static void applyTheme(String theme, JFrame frame) {
        try {
            switch (theme) {
                case "dark" -> {
                    FlatArcDarkIJTheme.setup();
                    currentTheme = "dark";
                }
                default -> {
                    FlatArcIJTheme.setup();
                    currentTheme = "light";
                }
            }

            SwingUtilities.updateComponentTreeUI(frame);
            frame.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }
}
