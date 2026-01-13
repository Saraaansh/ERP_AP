package edu.univ.erp.ui.components;

import javax.swing.*;
import java.awt.*;

public class AlertsBox extends JPanel {

    private final DefaultListModel<String> model;

    public AlertsBox() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        list.setFont(new Font("SansSerif", Font.PLAIN, 12));

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    public void setAlerts(java.util.List<String> alerts) {
        model.clear();
        for (String a : alerts) model.addElement(a);
    }
}
