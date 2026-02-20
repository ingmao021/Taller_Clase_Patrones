package com.academia.view;

import com.academia.view.panels.StudyPlansPanel;
import com.academia.view.panels.SubjectsPanel;
import com.academia.view.panels.TeachersPanel;
import com.academia.view.utils.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main application window for the Academic Management System.
 *
 * Organises navigation through three tabs:
 *  - Study Plans  (Builder + Prototype patterns)
 *  - Subjects
 *  - Teachers
 */
public class MainFrame extends JFrame {

    private StudyPlansPanel studyPlansPanel;
    private SubjectsPanel   subjectsPanel;
    private TeachersPanel   teachersPanel;

    public MainFrame() {
        super("Sistema de Gestión Académica");
        configureWindow();
        buildUI();
        setVisible(true);
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 720);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
    }

    private void buildUI() {

        // ── Top header bar ────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.HEADER_BG);
        header.setBorder(new EmptyBorder(14, 24, 14, 24));

        JPanel titleArea = new JPanel(new GridLayout(2, 1, 0, 2));
        titleArea.setOpaque(false);

        JLabel lblTitle = new JLabel("🎓  Sistema de Gestión Académica");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel(
            "  Patrón Builder  ·  Patrón Prototype  ·  Desarrollado con Java Swing"
        );
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(191, 219, 254));

        titleArea.add(lblTitle);
        titleArea.add(lblSubtitle);
        header.add(titleArea, BorderLayout.WEST);

        // Pattern indicator chips
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        chips.setOpaque(false);
        chips.add(buildChip("🔨 Builder",   new Color(37, 99, 235)));
        chips.add(buildChip("🧬 Prototype", new Color(22, 163, 74)));
        header.add(chips, BorderLayout.EAST);

        getContentPane().add(header, BorderLayout.NORTH);

        // ── Tab panel ─────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        studyPlansPanel = new StudyPlansPanel();
        subjectsPanel   = new SubjectsPanel();
        teachersPanel   = new TeachersPanel();

        tabs.addTab("  📋 Planes de Estudio  ", studyPlansPanel);
        tabs.addTab("  📚 Asignaturas  ",        subjectsPanel);
        tabs.addTab("  👨‍🏫 Docentes  ",           teachersPanel);

        // Refresh the active panel's data on every tab switch
        tabs.addChangeListener(e -> {
            switch (tabs.getSelectedIndex()) {
                case 0 -> studyPlansPanel.loadData();
                case 1 -> subjectsPanel.loadData();
                case 2 -> teachersPanel.loadData();
            }
        });

        getContentPane().add(tabs, BorderLayout.CENTER);

        // ── Status bar (footer) ───────────────────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(241, 245, 249));
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyle.BORDER_COLOR),
                new EmptyBorder(6, 16, 6, 16)
        ));
        JLabel lblStatus = new JLabel("Listo  ·  Los datos se almacenan en memoria durante la sesión.");
        lblStatus.setFont(UIStyle.SMALL);
        lblStatus.setForeground(UIStyle.TEXT_MUTED);
        footer.add(lblStatus, BorderLayout.WEST);

        JLabel lblVersion = new JLabel("v1.0  |  Java Swing");
        lblVersion.setFont(UIStyle.SMALL);
        lblVersion.setForeground(UIStyle.TEXT_MUTED);
        footer.add(lblVersion, BorderLayout.EAST);

        getContentPane().add(footer, BorderLayout.SOUTH);
    }

    /** Builds a small coloured label used as a pattern indicator chip. */
    private JLabel buildChip(String text, Color background) {
        JLabel chip = new JLabel(text);
        chip.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chip.setForeground(Color.WHITE);
        chip.setOpaque(true);
        chip.setBackground(background);
        chip.setBorder(new EmptyBorder(4, 10, 4, 10));
        return chip;
    }
}