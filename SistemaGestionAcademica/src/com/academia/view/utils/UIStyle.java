package com.academia.view.utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Utility class that provides a consistent visual style across the entire application.
 * All factory methods return pre-styled Swing components ready to use.
 */
public class UIStyle {

    // ── Color palette ─────────────────────────────────────────────────────────
    public static final Color PRIMARY      = new Color(26,  86,  219);
    public static final Color SECONDARY    = new Color(37,  99,  235);
    public static final Color ACCENT       = new Color(59,  130, 246);
    public static final Color SUCCESS      = new Color(22,  163, 74);
    public static final Color DANGER       = new Color(220, 38,  38);
    public static final Color WARNING      = new Color(217, 119, 6);
    public static final Color BACKGROUND   = new Color(248, 250, 252);
    public static final Color PANEL_BG     = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(226, 232, 240);
    public static final Color TEXT         = new Color(15,  23,  42);
    public static final Color TEXT_MUTED   = new Color(100, 116, 139);
    public static final Color HEADER_BG    = new Color(30,  64,  175);
    public static final Color HEADER_FG    = Color.WHITE;
    public static final Color ROW_EVEN     = new Color(241, 245, 249);
    public static final Color SELECTION    = new Color(219, 234, 254);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font TITLE         = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font SUBTITLE      = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font BODY          = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BUTTON        = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font LABEL         = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font TABLE         = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font TABLE_HEADER  = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font SMALL         = new Font("Segoe UI", Font.PLAIN, 11);

    // ── Component factories ───────────────────────────────────────────────────

    public static JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(BUTTON);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 18, 8, 18));
        button.setOpaque(true);
        return button;
    }

    public static JButton createPrimaryButton(String text)  { return createButton(text, PRIMARY); }
    public static JButton createSuccessButton(String text)  { return createButton(text, SUCCESS); }
    public static JButton createDangerButton(String text)   { return createButton(text, DANGER); }
    public static JButton createWarningButton(String text)  { return createButton(text, WARNING); }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUBTITLE);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL);
        label.setForeground(TEXT);
        return label;
    }

    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    public static JTextArea createTextArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setFont(BODY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(6, 10, 6, 10));
        return area;
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        );
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BG);
        panel.setBorder(createCardBorder());
        return panel;
    }

    /**
     * Applies the system Look & Feel and customises common table defaults.
     */
    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Table.font",                TABLE);
            UIManager.put("TableHeader.font",          TABLE_HEADER);
            UIManager.put("TableHeader.background",    PRIMARY);
            UIManager.put("TableHeader.foreground",    Color.WHITE);
            UIManager.put("Table.selectionBackground", SELECTION);
            UIManager.put("Table.selectionForeground", TEXT);
            UIManager.put("Table.alternateRowColor",   ROW_EVEN);
        } catch (Exception ignored) {
            // Fall back to default L&F
        }
    }
}