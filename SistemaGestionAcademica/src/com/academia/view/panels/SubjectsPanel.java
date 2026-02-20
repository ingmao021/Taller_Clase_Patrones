package com.academia.view.panels;

import com.academia.controller.AcademyController;
import com.academia.model.Subject;
import com.academia.view.utils.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing academic subjects.
 */
public class SubjectsPanel extends JPanel {

    private final AcademyController controller = AcademyController.getInstance();

    private JTable            table;
    private DefaultTableModel tableModel;

    public SubjectsPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UIStyle.BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        loadData();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title    = UIStyle.createTitleLabel("📚  Gestión de Asignaturas");
        JLabel subtitle = new JLabel("Administra las materias disponibles en el sistema.");
        subtitle.setFont(UIStyle.LABEL);
        subtitle.setForeground(UIStyle.TEXT_MUTED);
        JPanel titleArea = new JPanel(new GridLayout(2, 1, 0, 2));
        titleArea.setOpaque(false);
        titleArea.add(title);
        titleArea.add(subtitle);
        header.add(titleArea, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Nombre", "Créditos", "Modalidad", "Descripción"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(UIStyle.TABLE);
        table.getTableHeader().setFont(UIStyle.TABLE_HEADER);
        table.getTableHeader().setBackground(UIStyle.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(UIStyle.SELECTION);
        table.setGridColor(UIStyle.BORDER_COLOR);
        table.setShowGrid(true);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(2).setMaxWidth(80);
        table.getColumnModel().getColumn(3).setMaxWidth(110);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER_COLOR));
        add(scroll, BorderLayout.CENTER);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        JButton btnAdd    = UIStyle.createSuccessButton("+ Agregar Asignatura");
        JButton btnDelete = UIStyle.createDangerButton("✕ Eliminar");
        btnAdd.addActionListener(e    -> showAddDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), "Nueva Asignatura", true);
        dialog.setSize(440, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField tfName = UIStyle.createTextField(18);
        JSpinner spinCredits = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        spinCredits.setFont(UIStyle.BODY);
        JComboBox<Subject.Modality> cmbModality = new JComboBox<>(Subject.Modality.values());
        cmbModality.setFont(UIStyle.BODY);
        JTextArea taDescription = UIStyle.createTextArea(3, 18);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(UIStyle.createLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(tfName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(UIStyle.createLabel("Créditos:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(spinCredits, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(UIStyle.createLabel("Modalidad:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cmbModality, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(UIStyle.createLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(new JScrollPane(taDescription), gbc);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonRow.setBackground(UIStyle.BACKGROUND);
        JButton btnSave   = UIStyle.createSuccessButton("Guardar");
        JButton btnCancel = UIStyle.createDangerButton("Cancelar");

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            if (tfName.getText().isBlank()) {
                JOptionPane.showMessageDialog(dialog,
                        "El nombre es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.createSubject(
                    tfName.getText().trim(),
                    (Integer) spinCredits.getValue(),
                    taDescription.getText().trim(),
                    (Subject.Modality) cmbModality.getSelectedItem()
            );
            loadData();
            dialog.dispose();
        });

        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una asignatura.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la asignatura seleccionada?", "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteSubject(id);
            loadData();
        }
    }

    public void loadData() {
        tableModel.setRowCount(0);
        for (Subject s : controller.getSubjects()) {
            tableModel.addRow(new Object[]{
                s.getId(), s.getName(), s.getCredits(), s.getModality(), s.getDescription()
            });
        }
    }
}