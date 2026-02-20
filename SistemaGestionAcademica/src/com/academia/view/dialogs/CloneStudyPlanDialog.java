package com.academia.view.dialogs;

import com.academia.controller.AcademyController;
import com.academia.model.StudyPlan;
import com.academia.view.utils.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog for cloning an existing Study Plan using the PROTOTYPE pattern.
 *
 * The user selects a source plan, sets a new name and period, and the system
 * performs a deep copy of the entire plan (groups, teachers, schedules) via
 * {@link StudyPlan#clone()}.
 */
public class CloneStudyPlanDialog extends JDialog {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AcademyController controller = AcademyController.getInstance();
    private boolean planCloned = false;

    private JComboBox<StudyPlan> cmbSourcePlan;
    private JTextField           tfNewName;
    private JTextField           tfNewPeriod;
    private JTextField           tfStartDate;
    private JTextField           tfEndDate;
    private JTextArea            taSummary;

    public CloneStudyPlanDialog(Frame owner) {
        super(owner, "Clonar Plan de Estudio — Patrón Prototype", true);
        setSize(580, 500);
        setLocationRelativeTo(owner);
        buildUI();
        refreshSummary();
    }

    private void buildUI() {
        JPanel container = new JPanel(new BorderLayout(12, 12));
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Informational banner about the Prototype pattern
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(240, 253, 244));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        JLabel lblBanner = new JLabel(
            "<html><b>🧬 Patrón Prototype</b> — Clona un plan existente como base para un nuevo período.<br>" +
            "<small>Se realiza una copia profunda (deep copy): grupos, horarios y docentes se duplican completamente.</small></html>"
        );
        lblBanner.setFont(UIStyle.SMALL);
        lblBanner.setForeground(new Color(21, 128, 61));
        banner.add(lblBanner, BorderLayout.CENTER);
        container.add(banner, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Source plan selector
        List<StudyPlan> plans = controller.getStudyPlans();
        cmbSourcePlan = new JComboBox<>(plans.toArray(new StudyPlan[0]));
        cmbSourcePlan.setFont(UIStyle.BODY);
        cmbSourcePlan.addActionListener(e -> refreshSummary());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(UIStyle.createLabel("Plan a clonar:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cmbSourcePlan, gbc);

        // New name
        tfNewName = UIStyle.createTextField(20);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(UIStyle.createLabel("Nuevo nombre: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(tfNewName, gbc);

        // New period
        tfNewPeriod = UIStyle.createTextField(10);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(UIStyle.createLabel("Nuevo período: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(tfNewPeriod, gbc);

        // New dates
        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        dateRow.setOpaque(false);
        tfStartDate = UIStyle.createTextField(10);
        tfEndDate   = UIStyle.createTextField(10);
        tfStartDate.setText("01/08/2025");
        tfEndDate.setText("15/12/2025");
        dateRow.add(UIStyle.createLabel("Inicio:"));
        dateRow.add(tfStartDate);
        dateRow.add(UIStyle.createLabel("  Fin:"));
        dateRow.add(tfEndDate);
        dateRow.add(new JLabel("<html><small style='color:gray'>(dd/MM/yyyy)</small></html>"));
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(UIStyle.createLabel("Nuevas fechas:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(dateRow, gbc);

        // Summary of the source plan (read-only)
        taSummary = new JTextArea(5, 30);
        taSummary.setEditable(false);
        taSummary.setFont(UIStyle.SMALL);
        taSummary.setBackground(new Color(248, 250, 252));
        taSummary.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scrollSummary = new JScrollPane(taSummary);
        scrollSummary.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER_COLOR),
                "Resumen del plan original (lo que se clonará)"));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        form.add(scrollSummary, gbc);

        container.add(form, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setBackground(UIStyle.BACKGROUND);
        JButton btnCancel = UIStyle.createDangerButton("Cancelar");
        JButton btnClone  = UIStyle.createButton("🧬 Clonar Plan", UIStyle.SUCCESS);
        btnCancel.addActionListener(e -> dispose());
        btnClone.addActionListener(e  -> clonePlan());
        buttonRow.add(btnCancel);
        buttonRow.add(btnClone);
        container.add(buttonRow, BorderLayout.SOUTH);

        setContentPane(container);
    }

    /** Updates the summary text area when a different source plan is selected. */
    private void refreshSummary() {
        StudyPlan plan = (StudyPlan) cmbSourcePlan.getSelectedItem();
        if (plan == null) {
            taSummary.setText("No hay planes disponibles.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("📋 Nombre:    ").append(plan.getName()).append("\n");
        sb.append("📅 Período:   ").append(plan.getPeriod()).append("\n");
        sb.append("🎓 Programa:  ").append(plan.getProgram()).append("\n");
        sb.append("🏫 Modalidad: ").append(plan.getModality()).append("\n");
        sb.append("📌 Créditos:  ").append(plan.getTotalCredits()).append(" en total\n");
        sb.append("📦 Grupos:    ").append(plan.getGroups().size()).append(" grupo(s)\n");
        if (!plan.getGroups().isEmpty()) {
            sb.append("──────────────────────────────────────────\n");
            plan.getGroups().forEach(g ->
                sb.append("  • ").append(g.getName())
                  .append(" — ").append(g.getSubject().getName())
                  .append("\n")
            );
        }
        taSummary.setText(sb.toString());
    }

    private void clonePlan() {
        StudyPlan source = (StudyPlan) cmbSourcePlan.getSelectedItem();
        if (source == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay planes para clonar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar que los campos obligatorios no estén vacíos
        String newName = tfNewName.getText().trim();
        String newPeriod = tfNewPeriod.getText().trim();
        
        if (newName.isBlank() || newPeriod.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre y el período son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar que las fechas no estén vacías
        String startDateText = tfStartDate.getText().trim();
        String endDateText = tfEndDate.getText().trim();
        
        if (startDateText.isBlank() || endDateText.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Las fechas de inicio y fin son obligatorias.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            LocalDate start = LocalDate.parse(startDateText, DATE_FORMAT);
            LocalDate end   = LocalDate.parse(endDateText, DATE_FORMAT);

            controller.cloneStudyPlan(
                    source.getId(),
                    newName,
                    newPeriod,
                    start, end
            );

            planCloned = true;
            JOptionPane.showMessageDialog(this,
                    "✅ Plan clonado exitosamente con el patrón Prototype.\n" +
                    "Se realizó una copia profunda con " + source.getGroups().size() + " grupo(s).",
                    "Plan Clonado", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha incorrecto. Use dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isPlanCloned() {
        return planCloned;
    }
}
