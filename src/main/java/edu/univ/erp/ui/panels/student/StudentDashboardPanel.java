package edu.univ.erp.ui.panels.student;

import edu.univ.erp.ui.components.*;
import edu.univ.erp.ui.service.StudentDashboardDataProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentDashboardPanel extends JPanel {

    private final StudentDashboardDataProvider dataProvider;

    // Summary labels
    private JLabel currentRegValueLabel;
    private JLabel completedCoursesValueLabel;
    private JLabel termCreditsValueLabel;
    private JLabel cgpaValueLabel;

    // Timetable
    private DefaultTableModel todayTableModel;

    // Progress
    private JProgressBar programProgressBar;
    private JLabel programProgressTextLabel;

    // Alerts
    private DefaultListModel<String> alertsModel;

    // NEW: Big header text
    private JLabel headerLabel;

    // Maintenance
    private JLabel maintenanceBanner;

    private java.util.function.Consumer<String> onNavigate;

    public StudentDashboardPanel(StudentDashboardDataProvider provider,
            java.util.function.Consumer<String> onNavigate) {

        this.dataProvider = provider;
        this.onNavigate = onNavigate;

        setLayout(new BorderLayout());
        buildUI();
        loadDataAsync();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadDataAsync();
            }
        });
    }

    private void buildUI() {

        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ---------------------------------------------------
        // BIG HEADER: "Hello Test Student,"
        // ---------------------------------------------------
        headerLabel = new JLabel("Hello Student,", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        maintenanceBanner = new JLabel("Maintenance mode is ON — changes disabled.");
        maintenanceBanner.setOpaque(true);
        maintenanceBanner.setBackground(new Color(255, 230, 200));
        maintenanceBanner.setForeground(new Color(130, 60, 0));
        maintenanceBanner.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        maintenanceBanner.setVisible(false);

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(headerLabel, BorderLayout.NORTH);
        topWrapper.add(maintenanceBanner, BorderLayout.SOUTH);
        topWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        container.add(topWrapper, BorderLayout.NORTH);

        // MAIN SCROLL AREA
        JScrollPane scrollPane = new JScrollPane(buildDashboardContent());
        scrollPane.setBorder(null);

        container.add(scrollPane, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }

    private JPanel buildDashboardContent() {

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(buildSummaryCards());
        content.add(Box.createVerticalStrut(25));
        content.add(buildMiddleRow());
        content.add(Box.createVerticalStrut(25));
        content.add(buildBottomRow());

        return content;
    }

    private JPanel buildSummaryCards() {

        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 15));
        panel.setOpaque(false);

        currentRegValueLabel = new JLabel("0");
        completedCoursesValueLabel = new JLabel("0");
        termCreditsValueLabel = new JLabel("0");
        cgpaValueLabel = new JLabel("--");

        panel.add(new SummaryCard("Registrations", currentRegValueLabel, new Color(73, 156, 203)));
        panel.add(new SummaryCard("Completed", completedCoursesValueLabel, new Color(102, 187, 106)));
        panel.add(new SummaryCard("Term Credits", termCreditsValueLabel, new Color(255, 167, 38)));
        panel.add(new SummaryCard("CGPA", cgpaValueLabel, new Color(156, 39, 176)));

        return panel;
    }

    private JPanel buildMiddleRow() {

        JPanel row = new JPanel(new GridLayout(1, 2, 20, 20));
        row.setOpaque(false);

        // Quick Actions
        TitledContainer qaContainer = new TitledContainer("Quick Actions");
        JPanel qaPanel = new JPanel(new GridLayout(4, 1, 12, 12));
        qaPanel.setOpaque(false);

        qaPanel.add(new QuickActionButton("Course Catalog",
                () -> onNavigate.accept("catalog")));
        qaPanel.add(new QuickActionButton("My Registrations",
                () -> onNavigate.accept("registrations")));
        qaPanel.add(new QuickActionButton("View Grades",
                () -> onNavigate.accept("grades")));
        qaPanel.add(new QuickActionButton("Download Transcript",
                () -> onNavigate.accept("transcript")));

        qaContainer.setInnerComponent(qaPanel);

        // Today’s Classes
        TitledContainer ttc = new TitledContainer("Today’s Classes");
        todayTableModel = new DefaultTableModel(new Object[] { "Time", "Course", "Room" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(todayTableModel);
        table.setRowHeight(24);

        ttc.setInnerComponent(new JScrollPane(table));

        row.add(qaContainer);
        row.add(ttc);

        return row;
    }

    private JPanel buildBottomRow() {

        JPanel row = new JPanel(new GridLayout(1, 2, 20, 20));
        row.setOpaque(false);

        // Program Progress
        TitledContainer progress = new TitledContainer("Program Progress");
        JPanel pPanel = new JPanel(new BorderLayout());
        pPanel.setOpaque(false);

        programProgressBar = new JProgressBar(0, 100);
        programProgressBar.setStringPainted(true);

        programProgressTextLabel = new JLabel("Completed 0 of 0 credits");
        programProgressTextLabel.setBorder(BorderFactory.createEmptyBorder(8, 4, 0, 4));

        pPanel.add(programProgressBar, BorderLayout.NORTH);
        pPanel.add(programProgressTextLabel, BorderLayout.SOUTH);

        progress.setInnerComponent(pPanel);

        // Alerts
        TitledContainer alertsC = new TitledContainer("Alerts");
        alertsModel = new DefaultListModel<>();
        JList<String> alertList = new JList<>(alertsModel);

        alertsC.setInnerComponent(new JScrollPane(alertList));

        row.add(progress);
        row.add(alertsC);

        return row;
    }

    private void loadDataAsync() {

        SwingWorker<StudentDashboardDataProvider.StudentDashboardSnapshot, Void> worker = new SwingWorker<>() {

            @Override
            protected StudentDashboardDataProvider.StudentDashboardSnapshot doInBackground() {
                return dataProvider.loadSnapshot();
            }

            @Override
            protected void done() {
                try {
                    StudentDashboardDataProvider.StudentDashboardSnapshot s = get();
                    applySnapshot(s);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            StudentDashboardPanel.this,
                            "Error loading dashboard: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void applySnapshot(StudentDashboardDataProvider.StudentDashboardSnapshot s) {

        headerLabel.setText("Hello " + s.getStudentName() + ",");

        maintenanceBanner.setVisible(s.isMaintenanceOn());

        currentRegValueLabel.setText(String.valueOf(s.getCurrentRegistrations()));
        completedCoursesValueLabel.setText(String.valueOf(s.getCompletedCourses()));
        termCreditsValueLabel.setText(String.valueOf(s.getTermCredits()));

        if (Double.isNaN(s.getCgpa()))
            cgpaValueLabel.setText("--");
        else
            cgpaValueLabel.setText(String.format("%.2f", s.getCgpa()));

        // Today classes
        todayTableModel.setRowCount(0);

        for (StudentDashboardDataProvider.TodayClassRow t : s.getTodayClasses()) {
            todayTableModel.addRow(new Object[] {
                    t.getTimeRange(),
                    t.getCourseCode(),
                    t.getRoom()
            });
        }

        // Program Progress
        programProgressBar.setValue(s.getProgramPercent());
        programProgressTextLabel.setText(
                "Completed " + s.getCompletedCredits() +
                        " of " + s.getTotalProgramCredits());

        // Alerts
        alertsModel.clear();

        for (String alert : s.getAlerts()) {
            alertsModel.addElement(alert);
        }
    }
}
