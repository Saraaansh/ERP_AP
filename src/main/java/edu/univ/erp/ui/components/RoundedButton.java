package edu.univ.erp.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private int cornerRadius = 15;

    public RoundedButton(String text) {
        this(text, new Color(74, 144, 226)); // Default Primary Blue
    }

    public RoundedButton(String text, Color color) {
        super(text);
        this.normalColor = color;
        this.hoverColor = color.darker();
        this.pressedColor = color.darker().darker();

        setFont(new Font("SansSerif", Font.BOLD, 14));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Default size
        setPreferredSize(new Dimension(140, 38));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressedColor);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(hoverColor);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(pressedColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(normalColor != null ? normalColor : getBackground());
        }

        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, y);

        g2.dispose();
    }

    public void setNormalColor(Color color) {
        this.normalColor = color;
        this.hoverColor = color.darker();
        this.pressedColor = color.darker().darker();
        repaint();
    }
}
