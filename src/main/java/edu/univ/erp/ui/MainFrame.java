package edu.univ.erp.ui;

import edu.univ.erp.ui.service.RealInstructorDashboardProvider;
import edu.univ.erp.ui.service.RealStudentDashboardProvider;
import edu.univ.erp.ui.service.StudentDashboardDataProvider;
import edu.univ.erp.ui.components.Sidebar;
import edu.univ.erp.ui.components.TopBar;
import edu.univ.erp.ui.panels.SettingsPanel;
import edu.univ.erp.ui.panels.admin.AdminDashboardPanel;
import edu.univ.erp.ui.panels.admin.AdminSettingsPanel;
import edu.univ.erp.ui.panels.admin.CourseManagementPanel;
import edu.univ.erp.ui.panels.admin.LockedAccountsPanel;
import edu.univ.erp.ui.panels.admin.MaintenanceSettingsPanel;
import edu.univ.erp.ui.panels.admin.SectionManagementPanel;
import edu.univ.erp.ui.panels.admin.TermManagementPanel;
import edu.univ.erp.ui.panels.admin.UserManagementPanel;
import edu.univ.erp.ui.panels.instructor.*;
import edu.univ.erp.ui.panels.student.*;
import edu.univ.erp.ui.service.InstructorDashboardDataProvider;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private final String role; // "student", "instructor", "admin"

    private final Sidebar sidebar;
    private final TopBar topBar;

    // Student screens
    private StudentDashboardPanel studentDashboardPanel;
    private CourseCatalogPanel catalogPanel;
    private MyRegistrationsPanel registrationsPanel;
    private TimetablePanel timetablePanel;
    private GradesPanel gradesPanel;
    private TranscriptPanel transcriptPanel;
    private SettingsPanel studentSettingsPanel;

    // Instructor screens
    private InstructorDashboardPanel instrDashboardPanel;
    private InstructorCoursesPanel instrCoursesPanel;
    private InstructorCourseStudentsPanel instrStudentsPanel;
    private InstructorGradebookPanel instrGradebookPanel;
    private InstructorSectionDetailPanel instrSectionDetailPanel;
    private InstructorSettingsPanel instrSettingsPanel;
    private InstructorDashboardDataProvider instrProvider;
    private String currentSectionId;
    private String currentSectionLabel;

    // Admin screens
    private AdminDashboardPanel adminDashboardPanel;
    private UserManagementPanel adminUsersPanel;
    private CourseManagementPanel adminCoursesPanel;
    private SectionManagementPanel adminSectionsPanel;
    private TermManagementPanel adminTermsPanel;
    private LockedAccountsPanel adminLockedPanel;
    private MaintenanceSettingsPanel adminMaintPanel;
    private AdminSettingsPanel adminSettingsPanel;

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final Map<String, JPanel> screenMap = new HashMap<>();

    public MainFrame(String role) {
        this.role = role;

        setTitle("University ERP System");
        setSize(1250, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        topBar = new TopBar();
        sidebar = new Sidebar(role);
        sidebar.setSidebarListener(this::switchScreen);

        if ("student".equals(role)) {
            initStudentScreens();
        } else if ("instructor".equals(role)) {
            initInstructorScreens();
        } else if ("admin".equals(role)) {
            initAdminScreens();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Role '" + role + "' not implemented. Defaulting to student.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            initStudentScreens();
        }

        add(sidebar, BorderLayout.WEST);

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.add(topBar, BorderLayout.CENTER);
        topWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        add(topWrapper, BorderLayout.NORTH);

        add(contentPanel, BorderLayout.CENTER);

        // Default visible screen
        if ("instructor".equals(role)) {
            cardLayout.show(contentPanel, "instr_dashboard");
            sidebar.setActive("instr_dashboard");
        } else if ("admin".equals(role)) {
            cardLayout.show(contentPanel, "admin_dashboard");
            sidebar.setActive("admin_dashboard");
        } else {
            cardLayout.show(contentPanel, "dashboard");
            sidebar.setActive("dashboard");
        }

        // Start maintenance checker
        startMaintenanceChecker();
    }

    private void startMaintenanceChecker() {
        Timer timer = new Timer(30000, e -> checkMaintenance());
        timer.setInitialDelay(1000); // Check immediately after startup
        timer.start();
    }

    private void checkMaintenance() {
        SwingWorker<edu.univ.erp.domain.SystemSetting, Void> worker = new SwingWorker<>() {
            @Override
            protected edu.univ.erp.domain.SystemSetting doInBackground() {
                try {
                    return new edu.univ.erp.service.MaintenanceService().getCurrentSettings();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    edu.univ.erp.domain.SystemSetting s = get();
                    if (s != null) {
                        topBar.setMaintenanceMode(s.isMaintenanceMode(), s.getMessage());
                    }
                } catch (Exception ignore) {
                }
            }
        };
        worker.execute();
    }

    // ---------------- STUDENT ----------------

    private void initStudentScreens() {
        StudentDashboardDataProvider provider = new RealStudentDashboardProvider();
        StudentDashboardDataProvider.StudentDashboardSnapshot snap = provider.loadSnapshot();
        String studentName = snap.getStudentName();

        studentDashboardPanel = new StudentDashboardPanel(provider, this::switchScreen);
        catalogPanel = new CourseCatalogPanel();
        registrationsPanel = new MyRegistrationsPanel();
        timetablePanel = new TimetablePanel();
        gradesPanel = new GradesPanel();
        transcriptPanel = new TranscriptPanel();
        studentSettingsPanel = new SettingsPanel(studentName, this);

        screenMap.put("dashboard", studentDashboardPanel);
        screenMap.put("catalog", catalogPanel);
        screenMap.put("registrations", registrationsPanel);
        screenMap.put("timetable", timetablePanel);
        screenMap.put("grades", gradesPanel);
        screenMap.put("transcript", transcriptPanel);
        screenMap.put("settings", studentSettingsPanel);

        contentPanel.add(studentDashboardPanel, "dashboard");
        contentPanel.add(catalogPanel, "catalog");
        contentPanel.add(registrationsPanel, "registrations");
        contentPanel.add(timetablePanel, "timetable");
        contentPanel.add(gradesPanel, "grades");
        contentPanel.add(transcriptPanel, "transcript");
        contentPanel.add(studentSettingsPanel, "settings");
    }

    // ---------------- INSTRUCTOR ----------------

    private void initInstructorScreens() {
        instrProvider = new RealInstructorDashboardProvider();

        instrDashboardPanel = new InstructorDashboardPanel(instrProvider, this::switchScreen);
        instrStudentsPanel = new InstructorCourseStudentsPanel(instrProvider);
        instrGradebookPanel = new InstructorGradebookPanel(instrProvider);
        instrSectionDetailPanel = new InstructorSectionDetailPanel(instrProvider);
        instrSettingsPanel = new InstructorSettingsPanel();

        instrCoursesPanel = new InstructorCoursesPanel(
                instrProvider,
                (sectionId, label) -> {
                    currentSectionId = sectionId;
                    currentSectionLabel = label;

                    // ✅ both panels use (sectionId, label)
                    instrStudentsPanel.loadSection(sectionId, label);
                    instrGradebookPanel.loadSection(sectionId, label);
                    instrSectionDetailPanel.loadSection(sectionId, label);

                    cardLayout.show(contentPanel, "instr_gradebook");
                    sidebar.setActive("instr_gradebook");
                },
                (sectionId, label) -> {
                    currentSectionId = sectionId;
                    currentSectionLabel = label;

                    instrStudentsPanel.loadSection(sectionId, label);
                    // instrGradebookPanel.loadSection(sectionId, label); // Don't load gradebook
                    // yet
                    instrSectionDetailPanel.loadSection(sectionId, label);

                    cardLayout.show(contentPanel, "instr_details");
                    sidebar.setActive("instr_courses");
                });
        screenMap.put("instr_dashboard", instrDashboardPanel);
        screenMap.put("instr_courses", instrCoursesPanel);
        screenMap.put("instr_students", instrStudentsPanel);
        screenMap.put("instr_gradebook", instrGradebookPanel);
        screenMap.put("instr_details", instrSectionDetailPanel);
        screenMap.put("instr_settings", instrSettingsPanel);

        contentPanel.add(instrDashboardPanel, "instr_dashboard");
        contentPanel.add(instrCoursesPanel, "instr_courses");
        contentPanel.add(instrStudentsPanel, "instr_students");
        contentPanel.add(instrGradebookPanel, "instr_gradebook");
        contentPanel.add(instrSectionDetailPanel, "instr_details");
        contentPanel.add(instrSettingsPanel, "instr_settings");
    }

    // ---------------- ADMIN ----------------

    private void initAdminScreens() {
        adminDashboardPanel = new AdminDashboardPanel();
        adminUsersPanel = new UserManagementPanel();
        adminCoursesPanel = new CourseManagementPanel();
        adminSectionsPanel = new SectionManagementPanel();
        adminTermsPanel = new TermManagementPanel();
        adminLockedPanel = new LockedAccountsPanel();
        adminMaintPanel = new MaintenanceSettingsPanel();
        adminSettingsPanel = new AdminSettingsPanel();

        screenMap.put("admin_dashboard", adminDashboardPanel);
        screenMap.put("admin_users", adminUsersPanel);
        screenMap.put("admin_courses", adminCoursesPanel);
        screenMap.put("admin_sections", adminSectionsPanel);
        screenMap.put("admin_terms", adminTermsPanel);
        screenMap.put("admin_locked", adminLockedPanel);
        screenMap.put("admin_maint", adminMaintPanel);
        screenMap.put("admin_settings", adminSettingsPanel);

        contentPanel.add(adminDashboardPanel, "admin_dashboard");
        contentPanel.add(adminUsersPanel, "admin_users");
        contentPanel.add(adminCoursesPanel, "admin_courses");
        contentPanel.add(adminSectionsPanel, "admin_sections");
        contentPanel.add(adminTermsPanel, "admin_terms");
        contentPanel.add(adminLockedPanel, "admin_locked");
        contentPanel.add(adminMaintPanel, "admin_maint");
        contentPanel.add(adminSettingsPanel, "admin_settings");
    }

    // ---------------- SWITCHING ----------------

    private void switchScreen(String key) {
        if ("logout".equals(key)) {
            dispose();
            new LoginFrame().setVisible(true);
            return;
        }

        // Instructor special handling for students/details (but NOT gradebook)
        // Gradebook can now be opened directly - section selection happens inside
        if ("instructor".equals(role)) {
            if ("instr_students".equals(key) || "instr_details".equals(key)) {
                if (currentSectionId == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Select a section from 'Sections' first.",
                            "No Section Selected",
                            JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(contentPanel, "instr_courses");
                    sidebar.setActive("instr_courses");
                    return;
                }
                // make sure panels are loaded with the cached section
                if ("instr_students".equals(key))
                    instrStudentsPanel.loadSection(currentSectionId, currentSectionLabel);
                if ("instr_details".equals(key))
                    instrSectionDetailPanel.loadSection(currentSectionId, currentSectionLabel);
            } else if ("instr_gradebook".equals(key)) {
                if (currentSectionId != null) {
                    instrGradebookPanel.loadSection(currentSectionId, currentSectionLabel);
                }
            }
        }

        if (!screenMap.containsKey(key)) {
            return;
        }

        cardLayout.show(contentPanel, key);
        sidebar.setActive(key);
    }

    public static void main(String[] args) {
        edu.univ.erp.ui.util.ModernUI.setup();
        SwingUtilities.invokeLater(() -> new MainFrame("instructor").setVisible(true));
    }
}
