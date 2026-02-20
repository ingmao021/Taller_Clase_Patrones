package com.academia.view.dialogs;

import com.academia.controller.AcademyController;
import com.academia.model.Group;
import com.academia.model.Subject;
import com.academia.view.utils.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for creating a new Study Plan using the BUILDER pattern.
 *
 * Each form field corresponds to one step in the builder's construction
 * process, making the incremental assembly of the complex object explicit
 * and visible to the user.
 */
public class NewStudyPlanDialog extends JDialog {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AcademyController controller = AcademyController.getInstance();
    private boolean planCreated = false;

    // Form fields
    private JTextField              tfName;
    private JTextField              tfPeriod;
    private JTextField              tfProgram;
    private JComboBox<Subject.Modality> cmbModality;
    private JTextField              tfStartDate;
    private JTextField              tfEndDate;
    private JTextArea               taDescription;
    private JList<Group>            groupList;
    private DefaultListModel<Group> groupListModel;

    public NewStudyPlanDialog(Frame owner) {
        super(owner, "Nuevo Plan de Estudio — Patrón Builder", true);
        setSize(620, 620);
        setLocationRelativeTo(owner);
        buildUI();
    }

    private void buildUI() {
        JPanel container = new JPanel(new BorderLayout(12, 12));
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Informational banner about the Builder pattern
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(239, 246, 255));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        JLabel lblBanner = new JLabel(
            "<html><b>🔨 Patrón Builder</b> — Complete cada campo para construir el plan paso a paso.<br>" +
            "<small>El Builder valida que todos los campos obligatorios estén presentes antes de crear el objeto.</small></html>"
        );
        lblBanner.setFont(UIStyle.SMALL);
        lblBanner.setForeground(new Color(30, 64, 175));
        banner.add(lblBanner, BorderLayout.CENTER);
        container.add(banner, BorderLayout.NORTH);

        // Main form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        tfName        = UIStyle.createTextField(20);
        tfPeriod      = UIStyle.createTextField(10);
        tfProgram     = UIStyle.createTextField(20);
        cmbModality   = new JComboBox<>(Subject.Modality.values());
        cmbModality.setFont(UIStyle.BODY);
        tfStartDate   = UIStyle.createTextField(10);
        tfEndDate     = UIStyle.createTextField(10);
        taDescription = UIStyle.createTextArea(3, 20);
        tfStartDate.setText("01/02/2025");
        tfEndDate.setText("30/06/2025");

        addRow(form, gbc, 0, "Nombre del Plan: *", tfName);
        addRow(form, gbc, 1, "Período (ej. 2025-I): *", tfPeriod);
        addRow(form, gbc, 2, "Programa Académico: *", tfProgram);
        addRow(form, gbc, 3, "Modalidad:", cmbModality);

        // Date row
        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        dateRow.setOpaque(false);
        dateRow.add(UIStyle.createLabel("Inicio: *"));
        dateRow.add(tfStartDate);
        dateRow.add(UIStyle.createLabel("  Fin: *"));
        dateRow.add(tfEndDate);
        dateRow.add(new JLabel("<html><small style='color:gray'>(dd/MM/yyyy)</small></html>"));
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.gridwidth = 1;
        form.add(UIStyle.createLabel("Fechas:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(dateRow, gbc);

        // Description row
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(UIStyle.createLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        form.add(new JScrollPane(taDescription), gbc);

        // Group multi-selection list
        groupListModel = new DefaultListModel<>();
        for (Group g : controller.getGroups()) groupListModel.addElement(g);
        groupList = new JList<>(groupListModel);
        groupList.setFont(UIStyle.TABLE);
        groupList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        groupList.setVisibleRowCount(4);
        JScrollPane scrollGroups = new JScrollPane(groupList);
        scrollGroups.setPreferredSize(new Dimension(400, 100));
        TitledBorder groupBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER_COLOR),
                "Grupos a incluir (selección múltiple)");
        groupBorder.setTitleFont(UIStyle.LABEL);
        scrollGroups.setBorder(groupBorder);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.weightx = 1;
        form.add(scrollGroups, gbc);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Color.WHITE);
        center.add(form, BorderLayout.NORTH);
        container.add(center, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setBackground(UIStyle.BACKGROUND);
        JButton btnCancel = UIStyle.createDangerButton("Cancelar");
        JButton btnBuild  = UIStyle.createPrimaryButton("🔨 Construir Plan");
        btnCancel.addActionListener(e -> dispose());
        btnBuild.addActionListener(e  -> buildPlan());
        buttonRow.add(btnCancel);
        buttonRow.add(btnBuild);
        container.add(buttonRow, BorderLayout.SOUTH);

        setContentPane(container);
    }

    /** Utility: adds a label + component pair to the GridBag form. */
    private void addRow(JPanel form, GridBagConstraints gbc,
                        int row, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        form.add(UIStyle.createLabel(labelText), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(field, gbc);
    }

    private void buildPlan() {
        // Validar campos obligatorios
        String name = tfName.getText().trim();
        String period = tfPeriod.getText().trim();
        String program = tfProgram.getText().trim();
        String startDateText = tfStartDate.getText().trim();
        String endDateText = tfEndDate.getText().trim();
        
        if (name.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre del plan es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (period.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "El período es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (program.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "El programa académico es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (startDateText.isBlank() || endDateText.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Las fechas de inicio y fin son obligatorias.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            LocalDate start = LocalDate.parse(startDateText, DATE_FORMAT);
            LocalDate end   = LocalDate.parse(endDateText, DATE_FORMAT);

            List<Group> selectedGroups = new ArrayList<>(groupList.getSelectedValuesList());

            // Delegate to the controller which uses the StudyPlanBuilder internally
            controller.createStudyPlan(
                    name,
                    period,
                    program,
                    (Subject.Modality) cmbModality.getSelectedItem(),
                    start, end,
                    taDescription.getText().trim(),
                    selectedGroups
            );

            planCreated = true;
            JOptionPane.showMessageDialog(this,
                    "✅ Plan creado exitosamente con el patrón Builder.",
                    "Plan Creado", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha incorrecto. Use dd/MM/yyyy.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                    "Error de validación:\n" + e.getMessage(),
                    "Error del Builder", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isPlanCreated() {
        return planCreated;
    }
}