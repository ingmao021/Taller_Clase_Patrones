package com.academia.view.panels;

import com.academia.controller.AcademyController;
import com.academia.model.Teacher;
import com.academia.view.utils.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing teachers.
 * Allows adding, viewing and deleting teachers in the system.
 */
public class TeachersPanel extends JPanel {

    private final AcademyController controller = AcademyController.getInstance();

    private JTable             table;
    private DefaultTableModel  tableModel;

    public TeachersPanel() {
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
        JLabel title    = UIStyle.createTitleLabel("👨‍🏫  Gestión de Docentes");
        JLabel subtitle = new JLabel("Administra el cuerpo docente de la institución.");
        subtitle.setFont(UIStyle.LABEL);
        subtitle.setForeground(UIStyle.TEXT_MUTED);
        JPanel titleArea = new JPanel(new GridLayout(2, 1, 0, 2));
        titleArea.setOpaque(false);
        titleArea.add(title);
        titleArea.add(subtitle);
        header.add(titleArea, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Nombre", "Apellido", "Especialidad", "Email", "Teléfono"};
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

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER_COLOR));
        add(scroll, BorderLayout.CENTER);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        JButton btnAdd    = UIStyle.createSuccessButton("+ Agregar Docente");
        JButton btnDelete = UIStyle.createDangerButton("✕ Eliminar");
        btnAdd.addActionListener(e    -> showAddDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Docente", true);
        dialog.setSize(420, 370);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField tfFirstName   = UIStyle.createTextField(15);
        JTextField tfLastName    = UIStyle.createTextField(15);
        JTextField tfSpeciality  = UIStyle.createTextField(15);
        JTextField tfEmail       = UIStyle.createTextField(15);
        JTextField tfPhone       = UIStyle.createTextField(15);

        String[]      labels = {"Nombre:", "Apellido:", "Especialidad:", "Email:", "Teléfono:"};
        JTextField[]  fields = {tfFirstName, tfLastName, tfSpeciality, tfEmail, tfPhone};

        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            form.add(UIStyle.createLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            form.add(fields[i], gbc);
        }

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonRow.setBackground(UIStyle.BACKGROUND);
        JButton btnSave   = UIStyle.createSuccessButton("Guardar");
        JButton btnCancel = UIStyle.createDangerButton("Cancelar");

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            if (tfFirstName.getText().isBlank() || tfLastName.getText().isBlank()) {
                JOptionPane.showMessageDialog(dialog,
                        "El nombre y el apellido son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validar formato de email si se proporcionó
            String email = tfEmail.getText().trim();
            if (!email.isBlank() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(dialog,
                        "El formato del email no es válido.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            controller.createTeacher(
                    tfFirstName.getText().trim(),
                    tfLastName.getText().trim(),
                    tfSpeciality.getText().trim(),
                    email,
                    tfPhone.getText().trim()
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
                    "Seleccione un docente para eliminar.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al docente seleccionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteTeacher(id);
            loadData();
        }
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Teacher> list = controller.getTeachers();
        for (Teacher t : list) {
            tableModel.addRow(new Object[]{
                t.getId(), t.getFirstName(), t.getLastName(),
                t.getSpeciality(), t.getEmail(), t.getPhone()
            });
        }
    }
}