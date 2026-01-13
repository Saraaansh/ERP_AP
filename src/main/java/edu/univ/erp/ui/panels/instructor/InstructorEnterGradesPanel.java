package edu.univ.erp.ui.panels.instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InstructorEnterGradesPanel extends JPanel {

    private JLabel headerLabel;
    private DefaultTableModel model;

    public InstructorEnterGradesPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        buildUI();
    }

    private void buildUI() {
        headerLabel = new JLabel("Enter Grades");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        add(headerLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Assessment", "Score"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(22);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save (Mock)");
        saveBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Grades would be saved to backend here (mock).",
                "Save Grades",
                JOptionPane.INFORMATION_MESSAGE
        ));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(saveBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    public void setStudentAndSection(String studentName, String sectionLabel) {
        headerLabel.setText("Enter Grades – " + studentName + " / " + sectionLabel);
    }
}
