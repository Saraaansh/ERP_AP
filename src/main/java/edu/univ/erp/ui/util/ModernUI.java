package edu.univ.erp.ui.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class ModernUI {

    private static String currentTheme = "light";
    private static String currentFontSize = "medium";

    /**
     * Initial setup of the modern UI with FlatLaf theme.
     */
    public static void setup() {
        try {
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("ProgressBar.arc", 15);
            UIManager.put("TextComponent.arc", 15);

            UIManager.put("Button.background", new Color(52, 152, 219));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.hoverBackground", new Color(41, 128, 185));

            UIManager.put("Panel.background", new Color(245, 247, 250));
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("OptionPane.messageForeground", new Color(44, 62, 80));

            Font font = new Font("SansSerif", Font.PLAIN, 14);
            UIManager.put("defaultFont", font);

            FlatLightLaf.setup();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies theme (light or dark) to the entire application.
     * 
     * @param theme     "light" or "dark"
     * @param rootFrame the main JFrame to update
     */
    public static void applyTheme(String theme, JFrame rootFrame) {
        try {
            currentTheme = theme;

            if ("dark".equalsIgnoreCase(theme)) {
                FlatDarkLaf.setup();
                UIManager.put("Panel.background", new Color(30, 30, 30));
                UIManager.put("OptionPane.background", new Color(40, 40, 40));
                UIManager.put("Table.background", new Color(35, 35, 35));
                UIManager.put("Table.alternateRowColor", new Color(40, 40, 40));
            } else {
                FlatLightLaf.setup();
                UIManager.put("Panel.background", new Color(245, 247, 250));
                UIManager.put("OptionPane.background", Color.WHITE);
                UIManager.put("Table.background", Color.WHITE);
                UIManager.put("Table.alternateRowColor", new Color(250, 250, 250));
            }

            if (rootFrame != null) {
                SwingUtilities.updateComponentTreeUI(rootFrame);
                rootFrame.repaint();
                rootFrame.revalidate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates font size globally.
     * 
     * @param size "small", "medium", or "large"
     */
    public static void updateFontSize(String size) {
        currentFontSize = size;

        int baseSize;

        switch (size.toLowerCase()) {
            case "small":
                baseSize = 12;
                break;
            case "large":
                baseSize = 16;
                break;
            default:
                baseSize = 14;
                break;
        }

        Font baseFont = new Font("SansSerif", Font.PLAIN, baseSize);
        Font boldFont = new Font("SansSerif", Font.BOLD, baseSize);

        // Update UIManager defaults for components
        UIManager.put("defaultFont", baseFont);
        UIManager.put("Label.font", baseFont);
        UIManager.put("Button.font", baseFont);
        UIManager.put("TextField.font", baseFont);
        UIManager.put("TextArea.font", baseFont);
        UIManager.put("ComboBox.font", baseFont);
        UIManager.put("Table.font", baseFont);
        UIManager.put("Table.header.font", boldFont);
        UIManager.put("TableHeader.font", boldFont);
        UIManager.put("List.font", baseFont);
        UIManager.put("Menu.font", baseFont);
        UIManager.put("MenuItem.font", baseFont);
        UIManager.put("CheckBox.font", baseFont);
        UIManager.put("RadioButton.font", baseFont);
        UIManager.put("TabbedPane.font", baseFont);
        UIManager.put("Panel.font", baseFont);
        UIManager.put("OptionPane.font", baseFont);
        UIManager.put("OptionPane.messageFont", baseFont);
        UIManager.put("OptionPane.buttonFont", baseFont);
        UIManager.put("TitledBorder.font", boldFont);

        // Update all visible windows and their component trees
        for (Window window : Window.getWindows()) {
            if (window.isVisible()) {
                // Recursively update all components
                updateComponentTreeFonts(window, baseFont, boldFont, baseSize);
                SwingUtilities.updateComponentTreeUI(window);
                window.revalidate();
                window.repaint();
            }
        }
    }

    /**
     * Recursively updates fonts for all components in the tree.
     */
    private static void updateComponentTreeFonts(Container container, Font baseFont, Font boldFont, int baseSize) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                // Preserve bold/plain style but update size
                boolean wasBold = label.getFont() != null && label.getFont().isBold();
                label.setFont(wasBold ? boldFont : baseFont);
            } else if (comp instanceof JButton) {
                comp.setFont(baseFont);
            } else if (comp instanceof JTextField || comp instanceof JTextArea) {
                comp.setFont(baseFont);
            } else if (comp instanceof JTable) {
                JTable table = (JTable) comp;
                table.setFont(baseFont);
                if (table.getTableHeader() != null) {
                    table.getTableHeader().setFont(boldFont);
                }
            } else if (comp instanceof JList) {
                comp.setFont(baseFont);
            } else if (comp instanceof JComboBox) {
                comp.setFont(baseFont);
            } else if (comp instanceof JCheckBox || comp instanceof JRadioButton) {
                comp.setFont(baseFont);
            }

            // Recursively update children
            if (comp instanceof Container) {
                updateComponentTreeFonts((Container) comp, baseFont, boldFont, baseSize);
            }
        }
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static String getCurrentFontSize() {
        return currentFontSize;
    }
}
