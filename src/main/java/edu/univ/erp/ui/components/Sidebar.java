package edu.univ.erp.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Consumer;

/**
 * Modern sidebar with sky blue color scheme and 3-state navigation.
 */
public class Sidebar extends JPanel {

    private final String role;
    private String activeKey;
    private Consumer<String> listener;

    private final DefaultListModel<NavItem> topListModel = new DefaultListModel<>();
    private final DefaultListModel<NavItem> bottomListModel = new DefaultListModel<>();
    private final JList<NavItem> topNavList = new JList<>(topListModel);
    private final JList<NavItem> bottomNavList = new JList<>(bottomListModel);

    private final Color SKY_BLUE = new Color(76, 145, 210);
    private final Color LIGHT_SKY_BLUE = new Color(100, 165, 225);
    private final Color WHITE_TEXT = Color.WHITE;
    private final Color BLACK_TEXT = Color.BLACK;
    private final Color WHITE_BG = Color.WHITE;

    public Sidebar(String role) {
        this.role = role;
        setLayout(new BorderLayout());
        setBackground(SKY_BLUE);
        setPreferredSize(new Dimension(220, 0));

        buildUI();
    }

    /**
     * Builds the sidebar UI with header, top menu, and bottom menu sections.
     */
    private void buildUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SKY_BLUE);
        headerPanel.setBorder(new EmptyBorder(15, 18, 12, 18));

        JLabel roleLabel = new JLabel(capRole(role));
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        roleLabel.setForeground(WHITE_TEXT);
        headerPanel.add(roleLabel, BorderLayout.WEST);

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(100, 165, 225));
        separator.setBackground(new Color(100, 165, 225));

        JPanel separatorPanel = new JPanel(new BorderLayout());
        separatorPanel.setBackground(SKY_BLUE);
        separatorPanel.add(separator, BorderLayout.CENTER);
        separatorPanel.setPreferredSize(new Dimension(0, 1));

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(SKY_BLUE);
        topSection.add(headerPanel, BorderLayout.CENTER);
        topSection.add(separatorPanel, BorderLayout.SOUTH);

        setupList(topNavList);
        setupList(bottomNavList);

        JPanel topMenuPanel = new JPanel(new BorderLayout());
        topMenuPanel.setBackground(SKY_BLUE);
        topMenuPanel.setBorder(new EmptyBorder(8, 10, 10, 10));
        topMenuPanel.add(topNavList, BorderLayout.CENTER);

        JPanel bottomMenuPanel = new JPanel(new BorderLayout());
        bottomMenuPanel.setBackground(SKY_BLUE);
        bottomMenuPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        bottomMenuPanel.add(bottomNavList, BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(topMenuPanel, BorderLayout.CENTER);
        add(bottomMenuPanel, BorderLayout.SOUTH);

        populateMenu();
    }

    /**
     * Sets up a navigation list with custom renderer and mouse listeners.
     */
    private void setupList(JList<NavItem> list) {
        list.setBackground(SKY_BLUE);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        NavItemRenderer renderer = new NavItemRenderer();
        list.setCellRenderer(renderer);
        list.setFixedCellHeight(46);

        list.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index != renderer.hoveredIndex) {
                    renderer.hoveredIndex = index;
                    list.repaint();
                }
            }
        });

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                renderer.hoveredIndex = -1;
                list.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index >= 0) {
                    NavItem item = list.getModel().getElementAt(index);
                    if (listener != null) {
                        listener.accept(item.key);
                    }
                }
            }
        });
    }

    /**
     * Populates top and bottom menu sections based on user role.
     */
    private void populateMenu() {
        topListModel.clear();
        bottomListModel.clear();

        if ("student".equals(role)) {
            topListModel.addElement(new NavItem("Dashboard", "dashboard"));
            topListModel.addElement(new NavItem("Course Catalog", "catalog"));
            topListModel.addElement(new NavItem("My Registrations", "registrations"));
            topListModel.addElement(new NavItem("Timetable", "timetable"));
            topListModel.addElement(new NavItem("Grades", "grades"));
            topListModel.addElement(new NavItem("Transcript", "transcript"));

            bottomListModel.addElement(new NavItem("Settings", "settings"));
            bottomListModel.addElement(new NavItem("Logout", "logout"));
        } else if ("instructor".equals(role)) {
            topListModel.addElement(new NavItem("Dashboard", "instr_dashboard"));
            topListModel.addElement(new NavItem("Sections", "instr_courses"));
            topListModel.addElement(new NavItem("Gradebook", "instr_gradebook"));

            bottomListModel.addElement(new NavItem("Settings", "instr_settings"));
            bottomListModel.addElement(new NavItem("Logout", "logout"));
        } else if ("admin".equals(role)) {
            topListModel.addElement(new NavItem("Dashboard", "admin_dashboard"));
            topListModel.addElement(new NavItem("Users", "admin_users"));
            topListModel.addElement(new NavItem("Courses", "admin_courses"));
            topListModel.addElement(new NavItem("Sections", "admin_sections"));
            topListModel.addElement(new NavItem("Term Management", "admin_terms"));
            topListModel.addElement(new NavItem("Locked Accounts", "admin_locked"));
            topListModel.addElement(new NavItem("Maintenance", "admin_maint"));

            bottomListModel.addElement(new NavItem("Settings", "admin_settings"));
            bottomListModel.addElement(new NavItem("Logout", "logout"));
        }
    }

    private String capRole(String r) {
        if (r == null || r.isEmpty())
            return "";
        return r.substring(0, 1).toUpperCase() + r.substring(1);
    }

    public void setSidebarListener(Consumer<String> listener) {
        this.listener = listener;
    }

    /**
     * Sets the active menu item by key.
     */
    public void setActive(String key) {
        this.activeKey = key;
        topNavList.repaint();
        bottomNavList.repaint();
    }

    /**
     * Navigation item data class.
     */
    private static class NavItem {
        String label;
        String key;

        NavItem(String label, String key) {
            this.label = label;
            this.key = key;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /**
     * Custom cell renderer implementing 3-state logic:
     * DEFAULT: sky blue bg, white text
     * HOVER: lighter sky blue bg, white text
     * SELECTED: white rounded pill bg, black text
     */
    private class NavItemRenderer extends JLabel implements ListCellRenderer<NavItem> {
        private boolean isSelected;
        private boolean isHovered;
        int hoveredIndex = -1;
        private int currentIndex;

        public NavItemRenderer() {
            setOpaque(false);
            setFont(new Font("SansSerif", Font.PLAIN, 17));
            setBorder(new EmptyBorder(10, 14, 10, 14));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends NavItem> list, NavItem value,
                int index, boolean isSelected, boolean cellHasFocus) {

            setText(value.label);
            this.currentIndex = index;
            this.isSelected = (value.key.equals(activeKey));
            this.isHovered = (index == hoveredIndex && !this.isSelected);

            if (this.isSelected) {
                setForeground(BLACK_TEXT);
            } else {
                setForeground(WHITE_TEXT);
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isSelected) {
                g2.setColor(WHITE_BG);
                g2.fillRoundRect(4, 2, getWidth() - 8, getHeight() - 4, 18, 18);
            } else if (isHovered) {
                g2.setColor(LIGHT_SKY_BLUE);
                g2.fillRoundRect(4, 2, getWidth() - 8, getHeight() - 4, 18, 18);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
