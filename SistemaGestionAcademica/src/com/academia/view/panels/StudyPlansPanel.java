package com.academia.view.panels;

import com.academia.controller.AcademyController;
import com.academia.model.Group;
import com.academia.model.StudyPlan;
import com.academia.view.dialogs.CloneStudyPlanDialog;
import com.academia.view.dialogs.NewStudyPlanDialog;
import com.academia.view.utils.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Main panel for Study Plan management.
 *
 * Provides the entry points for:
 *  - Creating a new plan via the BUILDER pattern.
 *  - Cloning an existing plan via the PROTOTYPE pattern.
 *  - Viewing and deleting plans.
 *  - Inspecting the groups within a selected plan.
 */
public class StudyPlansPanel extends JPanel {

    private final AcademyController controller = AcademyController.getInstance();

    private JTable            plansTable;
    private DefaultTableModel plansModel;
    private JTable            groupsTable;
    private DefaultTableModel groupsModel;
    private JLabel            lblDetail;

    public StudyPlansPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UIStyle.BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        loadData();
    }

    private void buildUI() {

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title    = UIStyle.createTitleLabel("📋  Planes de Estudio");
        JLabel subtitle = new JLabel("Crea nuevos planes con Builder · Duplica períodos anteriores con Prototype");
        subtitle.setFont(UIStyle.LABEL);
        subtitle.setForeground(UIStyle.TEXT_MUTED);
        JPanel titleArea = new JPanel(new GridLayout(2, 1, 0, 2));
        titleArea.setOpaque(false);
        titleArea.add(title);
        titleArea.add(subtitle);
        header.add(titleArea, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ── Split pane: plans table (top) + group detail (bottom) ─────────────
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.55);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);

        // Plans table
        String[] planColumns = {"ID", "Nombre", "Período", "Programa", "Modalidad", "Grupos", "Créditos"};
        plansModel = new DefaultTableModel(planColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        plansTable = new JTable(plansModel);
        configureTable(plansTable, UIStyle.PRIMARY);
        plansTable.getColumnModel().getColumn(0).setMaxWidth(60);
        plansTable.getColumnModel().getColumn(4).setMaxWidth(100);
        plansTable.getColumnModel().getColumn(5).setMaxWidth(70);
        plansTable.getColumnModel().getColumn(6).setMaxWidth(80);
        plansTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showGroupDetail();
        });
        JScrollPane scrollPlans = new JScrollPane(plansTable);
        scrollPlans.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER_COLOR));

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        buttonPanel.setOpaque(false);
        JButton btnNew    = UIStyle.createPrimaryButton("🔨 Nuevo Plan (Builder)");
        JButton btnClone  = UIStyle.createButton("🧬 Clonar Plan (Prototype)", UIStyle.SUCCESS);
        JButton btnDelete = UIStyle.createDangerButton("✕ Eliminar");
        btnNew.addActionListener(e    -> openNewPlanDialog());
        btnClone.addActionListener(e  -> openClonePlanDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        buttonPanel.add(btnNew);
        buttonPanel.add(btnClone);
        buttonPanel.add(btnDelete);

        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setOpaque(false);
        topPanel.add(scrollPlans, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        splitPane.setTopComponent(topPanel);

        // ── Group detail panel ────────────────────────────────────────────────
        JPanel detailPanel = new JPanel(new BorderLayout(0, 6));
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        lblDetail = UIStyle.createSubtitleLabel("Grupos del Plan");
        lblDetail.setForeground(UIStyle.TEXT_MUTED);
        detailPanel.add(lblDetail, BorderLayout.NORTH);

        String[] groupColumns = {"Grupo", "Asignatura", "Créditos", "Docente", "Horario", "Cupos"};
        groupsModel = new DefaultTableModel(groupColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        groupsTable = new JTable(groupsModel);
        configureTable(groupsTable, UIStyle.ACCENT);
        JScrollPane scrollGroups = new JScrollPane(groupsTable);
        scrollGroups.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER_COLOR));
        detailPanel.add(scrollGroups, BorderLayout.CENTER);
        splitPane.setBottomComponent(detailPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private void configureTable(JTable table, Color headerColor) {
        table.setRowHeight(32);
        table.setFont(UIStyle.TABLE);
        table.getTableHeader().setFont(UIStyle.TABLE_HEADER);
        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(UIStyle.SELECTION);
        table.setGridColor(UIStyle.BORDER_COLOR);
        table.setShowGrid(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void openNewPlanDialog() {
        NewStudyPlanDialog dialog = new NewStudyPlanDialog(
                (Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.isPlanCreated()) loadData();
    }

    private void openClonePlanDialog() {
        List<StudyPlan> plans = controller.getStudyPlans();
        if (plans.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay planes existentes para clonar. Cree primero uno con el Builder.",
                    "Sin planes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        CloneStudyPlanDialog dialog = new CloneStudyPlanDialog(
                (Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.isPlanCloned()) loadData();
    }

    private void deleteSelected() {
        int row = plansTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un plan para eliminar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) plansModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el plan seleccionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteStudyPlan(id);
            groupsModel.setRowCount(0);
            lblDetail.setText("Grupos del Plan");
            loadData();
        }
    }

    /** Populates the bottom groups table based on the currently selected plan. */
    private void showGroupDetail() {
        groupsModel.setRowCount(0);
        int row = plansTable.getSelectedRow();
        if (row < 0) return;

        String id = (String) plansModel.getValueAt(row, 0);
        if (id == null) return;
        
        StudyPlan plan = controller.getStudyPlanById(id);
        if (plan == null) return;

        lblDetail.setText("Grupos del plan: " + plan.getName() + "  (" + plan.getPeriod() + ")");
        lblDetail.setForeground(UIStyle.TEXT);

        for (Group g : plan.getGroups()) {
            if (g == null) continue;
            groupsModel.addRow(new Object[]{
                g.getName(),
                g.getSubject() != null ? g.getSubject().getName() : "N/A",
                g.getSubject() != null ? g.getSubject().getCredits() + " cr." : "N/A",
                g.getTeacher() != null ? g.getTeacher().getFullName() : "N/A",
                g.getSchedule() != null ? g.getSchedule().toString() : "N/A",
                g.getMaxSlots() + " cupos"
            });
        }
    }

    public void loadData() {
        plansModel.setRowCount(0);
        for (StudyPlan p : controller.getStudyPlans()) {
            plansModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getPeriod(),
                p.getProgram(), p.getModality(),
                p.getGroups().size(), p.getTotalCredits()
            });
        }
    }
}