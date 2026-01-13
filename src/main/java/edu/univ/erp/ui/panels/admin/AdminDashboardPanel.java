package edu.univ.erp.ui.panels.admin;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.ui.components.SummaryCard;
import edu.univ.erp.ui.components.TitledContainer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminDashboardPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final SectionDAO sectionDAO = new SectionDAO();

    private JLabel studentsCountLabel;
    private JLabel instructorsCountLabel;
    private JLabel coursesCountLabel;
    private JLabel sectionsCountLabel;

    public AdminDashboardPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        add(buildHeader(), BorderLayout.NORTH);
        add(buildStatsRow(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        // Load data AFTER UI is rendered, never in constructor
        SwingUtilities.invokeLater(this::loadRealStats);
    }

    private JComponent buildHeader() {
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return title;
    }

    private JComponent buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 15, 15));
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        row.setOpaque(false);

        studentsCountLabel = new JLabel("0");
        instructorsCountLabel = new JLabel("0");
        coursesCountLabel = new JLabel("0");
        sectionsCountLabel = new JLabel("0");

        row.add(new SummaryCard("Students", studentsCountLabel, new Color(52, 152, 219)));
        row.add(new SummaryCard("Instructors", instructorsCountLabel, new Color(46, 204, 113)));
        row.add(new SummaryCard("Courses", coursesCountLabel, new Color(243, 156, 18)));
        row.add(new SummaryCard("Sections", sectionsCountLabel, new Color(155, 89, 182)));

        return row;
    }

    private JComponent buildFooter() {
        TitledContainer container = new TitledContainer("System Status & Notes");

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JTextArea notes = new JTextArea(
                "• System running in NORMAL mode.\n" +
                        "• No pending schema migrations.\n" +
                        "• Remember to backup DB weekly.");
        notes.setEditable(false);
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setFont(new Font("SansSerif", Font.PLAIN, 14));
        notes.setOpaque(false);
        notes.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(new JScrollPane(notes), BorderLayout.CENTER);

        container.setInnerComponent(panel);
        container.setPreferredSize(new Dimension(0, 200));

        return container;
    }

    private void loadRealStats() {
        SwingWorker<int[], Void> worker = new SwingWorker<>() {
            @Override
            protected int[] doInBackground() {
                int[] counts = new int[4];
                try {
                    // Count students
                    List<Student> students = studentDAO.getAll();
                    counts[0] = students != null ? students.size() : 0;

                    // Count instructors
                    List<Instructor> instructors = instructorDAO.getAll();
                    counts[1] = instructors != null ? instructors.size() : 0;

                    // Count courses
                    List<Course> courses = courseDAO.getAll();
                    counts[2] = courses != null ? courses.size() : 0;

                    // Count sections
                    List<Section> sections = sectionDAO.getAllSections();
                    counts[3] = sections != null ? sections.size() : 0;

                } catch (Exception e) {
                    // Silent failure - return -1 to indicate error
                    counts[0] = counts[1] = counts[2] = counts[3] = -1;
                }
                return counts;
            }

            @Override
            protected void done() {
                try {
                    int[] counts = get();

                    // Display real counts or "—" on failure
                    studentsCountLabel.setText(counts[0] >= 0 ? String.valueOf(counts[0]) : "—");
                    instructorsCountLabel.setText(counts[1] >= 0 ? String.valueOf(counts[1]) : "—");
                    coursesCountLabel.setText(counts[2] >= 0 ? String.valueOf(counts[2]) : "—");
                    sectionsCountLabel.setText(counts[3] >= 0 ? String.valueOf(counts[3]) : "—");

                } catch (Exception e) {
                    // Silent failure during initialization
                    studentsCountLabel.setText("—");
                    instructorsCountLabel.setText("—");
                    coursesCountLabel.setText("—");
                    sectionsCountLabel.setText("—");
                }
            }
        };
        worker.execute();
    }
}
