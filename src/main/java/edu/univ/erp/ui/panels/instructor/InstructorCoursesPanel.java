package edu.univ.erp.ui.panels.instructor;

import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;
import edu.univ.erp.ui.service.InstructorDashboardDataProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;

public class InstructorCoursesPanel extends JPanel {

    private final InstructorDashboardDataProvider provider;
    private final BiConsumer<String, String> onOpenSection;
    private final BiConsumer<String, String> onOpenDetails;

    private DefaultTableModel sectionModel;
    private JTable sectionTable;

    public InstructorCoursesPanel(InstructorDashboardDataProvider provider,
            BiConsumer<String, String> onOpenSection,
            BiConsumer<String, String> onOpenDetails) {
        this.provider = provider;
        this.onOpenSection = onOpenSection;
        this.onOpenDetails = onOpenDetails;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        buildUI();
        loadSections();
    }

    private void buildUI() {
        TitledContainer container = new TitledContainer("Sections This Term");

        sectionModel = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Section ID", "Course Code", "Term" }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        sectionTable = new JTable(sectionModel);
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

        // double-click to open
        sectionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && sectionTable.getSelectedRow() >= 0) {
                    openSelectedSection();
                }
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        JScrollPane scroll = new JScrollPane(sectionTable);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);

        RoundedButton openBtn = new RoundedButton("Open Gradebook", new Color(52, 152, 219));
        openBtn.addActionListener(e -> openSelectedSection());

        RoundedButton detailsBtn = new RoundedButton("Manage Assessments", new Color(155, 89, 182));
        detailsBtn.addActionListener(e -> openSelectedDetails());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottom.setOpaque(false);
        bottom.add(detailsBtn);
        bottom.add(openBtn);

        root.add(bottom, BorderLayout.SOUTH);

        container.setInnerComponent(root);
        add(container, BorderLayout.CENTER);
    }

    private void loadSections() {
        sectionModel.setRowCount(0);
        List<InstructorDashboardDataProvider.SectionRow> rows = provider.loadSections();
        for (InstructorDashboardDataProvider.SectionRow r : rows) {
            sectionModel.addRow(new Object[] {
                    r.getSectionId(),
                    r.getCourseCode(),
                    r.getTerm()
            });
        }
    }

    private void openSelectedSection() {
        int row = sectionTable.getSelectedRow();
        if (row < 0)
            return;

        String sectionId = (String) sectionModel.getValueAt(row, 0);
        String courseCode = (String) sectionModel.getValueAt(row, 1);
        String label = courseCode + " (" + sectionId + ")";

        if (onOpenSection != null) {
            onOpenSection.accept(sectionId, label);
        }
    }

    private void openSelectedDetails() {
        int row = sectionTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a section first.");
            return;
        }

        String sectionId = (String) sectionModel.getValueAt(row, 0);
        String courseCode = (String) sectionModel.getValueAt(row, 1);
        String label = courseCode + " (" + sectionId + ")";

        if (onOpenDetails != null) {
            onOpenDetails.accept(sectionId, label);
        }
    }
}
