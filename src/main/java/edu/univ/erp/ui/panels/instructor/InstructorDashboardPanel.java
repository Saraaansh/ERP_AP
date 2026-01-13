package edu.univ.erp.ui.panels.instructor;

import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.SummaryCard;
import edu.univ.erp.ui.components.TitledContainer;
import edu.univ.erp.ui.service.InstructorDashboardDataProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class InstructorDashboardPanel extends JPanel {

    private final InstructorDashboardDataProvider provider;
    private final Consumer<String> onNavigate;

    private JLabel nameLabel;
    private JLabel termLabel;
    private JLabel sectionsValueLabel;
    private JLabel studentsValueLabel;
    private JLabel pendingValueLabel;
    private JTable sectionTable;
    private DefaultListModel<String> alertsModel;

    public InstructorDashboardPanel(InstructorDashboardDataProvider provider,
            Consumer<String> onNavigate) {
        this.provider = provider;
        this.onNavigate = onNavigate;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        buildUI();
        loadData();
    }

    private void buildUI() {
        // top header
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        nameLabel = new JLabel("Welcome, Instructor");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        nameLabel.setForeground(new Color(44, 62, 80));

        termLabel = new JLabel("Current Term");
        termLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        termLabel.setForeground(Color.GRAY);

        JPanel nameBox = new JPanel();
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));
        nameBox.setOpaque(false);
        nameBox.add(nameLabel);
        nameBox.add(Box.createVerticalStrut(5));
        nameBox.add(termLabel);

        top.add(nameBox, BorderLayout.WEST);
        top.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(top, BorderLayout.NORTH);

        // center content
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        // summary row
        JPanel summaryRow = new JPanel(new GridLayout(1, 3, 15, 15));
        summaryRow.setOpaque(false);

        sectionsValueLabel = new JLabel("0");
        studentsValueLabel = new JLabel("0");
        pendingValueLabel = new JLabel("0");

        summaryRow.add(new SummaryCard("Sections this Term", sectionsValueLabel, new Color(52, 152, 219)));
        summaryRow.add(new SummaryCard("Total Students", studentsValueLabel, new Color(46, 204, 113)));
        summaryRow.add(new SummaryCard("Pending Grading", pendingValueLabel, new Color(231, 76, 60)));

        center.add(summaryRow);
        center.add(Box.createVerticalStrut(25));

        // sections table
        TitledContainer sectionsContainer = new TitledContainer("Your Sections");
        sectionTable = new JTable(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] { "Section ID", "Course", "Term" }) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        sectionTable.setRowHeight(32);
        sectionTable.setShowVerticalLines(false);
        sectionTable.setIntercellSpacing(new Dimension(0, 0));

        // Header styling
        JTableHeader header = sectionTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(236, 236, 236));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));

        // Zebra striping
        sectionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : Color.WHITE);
                }
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(sectionTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        sectionsContainer.setInnerComponent(scrollPane);

        // quick actions
        JPanel quick = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        quick.setOpaque(false);

        RoundedButton openSectionsBtn = new RoundedButton("Open Sections", new Color(52, 152, 219));
        RoundedButton openGradebookBtn = new RoundedButton("Go to Gradebook", new Color(155, 89, 182));

        openSectionsBtn.addActionListener(e -> onNavigate.accept("instr_courses"));
        openGradebookBtn.addActionListener(e -> onNavigate.accept("instr_gradebook"));

        quick.add(openSectionsBtn);
        quick.add(openGradebookBtn);

        JPanel middle = new JPanel(new BorderLayout());
        middle.setOpaque(false);
        middle.add(sectionsContainer, BorderLayout.CENTER);
        middle.add(quick, BorderLayout.SOUTH);

        center.add(middle);
        center.add(Box.createVerticalStrut(25));

        // alerts
        TitledContainer alerts = new TitledContainer("Alerts");
        alertsModel = new DefaultListModel<>();
        JList<String> list = new JList<>(alertsModel);
        list.setFont(new Font("SansSerif", Font.PLAIN, 14));
        alerts.setInnerComponent(new JScrollPane(list));

        center.add(alerts);

        add(center, BorderLayout.CENTER);
    }

    private void loadData() {
        InstructorDashboardDataProvider.InstructorSummary s = provider.loadSummary();
        nameLabel.setText("Welcome, " + s.getInstructorName());
        termLabel.setText(s.getTermLabel());

        sectionsValueLabel.setText(String.valueOf(s.getSectionsThisTerm()));
        studentsValueLabel.setText(String.valueOf(s.getTotalStudents()));
        pendingValueLabel.setText(String.valueOf(s.getPendingGrading()));

        // sections list from provider
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) sectionTable.getModel();
        model.setRowCount(0);

        List<InstructorDashboardDataProvider.SectionRow> rows = provider.loadSections();
        for (InstructorDashboardDataProvider.SectionRow r : rows) {
            model.addRow(new Object[] {
                    r.getSectionId(),
                    r.getCourseCode(),
                    r.getTerm()
            });
        }

        // alerts
        alertsModel.clear();
        alertsModel.addElement("Welcome to the Instructor Dashboard.");
        alertsModel.addElement("All data is loaded from the database.");
    }
}
