package edu.univ.erp.ui.panels.instructor;
import edu.univ.erp.ui.service.InstructorDashboardDataProvider;
import edu.univ.erp.ui.components.TitledContainer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.function.Consumer;

public class InstructorSectionsPanel extends JPanel {

    private final InstructorDashboardDataProvider dataProvider;
    private final Consumer<String> openSectionCallback;

    private DefaultTableModel model;

    public InstructorSectionsPanel(InstructorDashboardDataProvider provider,
                                   Consumer<String> openSectionCallback) {

        this.dataProvider = provider;
        this.openSectionCallback = openSectionCallback;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        buildUI();
        loadSections();
    }

    private void buildUI() {

        TitledContainer tc = new TitledContainer("My Sections");
        tc.setInnerComponent(buildTable());
        add(tc, BorderLayout.CENTER);
    }

    private JScrollPane buildTable() {

        model = new DefaultTableModel(
                new Object[]{"Section ID", "Course Code", "Term", "Action"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 3;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);

        table.getColumn("Action").setCellRenderer((tbl, val, isSel, hasF, r, c) -> {
            JButton btn = new JButton("Open");
            return btn;
        });

        table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(
                    JTable tbl, Object val, boolean selected, int row, int col) {

                JButton btn = new JButton("Open");
                String sectionId = tbl.getValueAt(row, 0).toString();
                btn.addActionListener(e -> openSectionCallback.accept(sectionId));
                return btn;
            }
        });

        return new JScrollPane(table);
    }

    private void loadSections() {
        var sections = dataProvider.loadSections();
        model.setRowCount(0);
        for (var s : sections) {
            model.addRow(new Object[]{
                    s.getSectionId(),
                    s.getCourseCode(),
                    s.getTerm(),
                    "Open"
            });
        }
    }
}
