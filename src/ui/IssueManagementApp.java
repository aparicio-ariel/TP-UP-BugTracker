package ui;

import database.DatabaseManager;
import model.IssueHistory;
import model.User;
import model.UserContext;
import service.IssueHistoryService;
import service.IssueServiceImpl;
import model.Issue;
import utils.Utils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class IssueManagementApp extends JFrame {

    private IssueServiceImpl issueServiceImpl;
    private JTextField descriptionField;
    private JTextField estimatedHoursField;
    private JTextField actualHoursField;
    private JComboBox<String> statusComboBox;
    private JTable issueTable;
    private IssueTableModel issueTableModel;
    private Long projectId;
    private JButton historyButton;

    public IssueManagementApp(Long projectId) {
        this.projectId = projectId;
        issueServiceImpl = new IssueServiceImpl();

        setTitle("Gestión de Incidentes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSplitPane(), BorderLayout.CENTER);

        loadIssues();
    }


    private JSplitPane createSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.3);
        splitPane.setTopComponent(createInputPanel());
        splitPane.setBottomComponent(createTablePanel());
        return splitPane;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Información del Incidente"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        descriptionField = new JTextField(20);
        inputPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Horas Estimadas:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        estimatedHoursField = new JTextField(10);
        inputPanel.add(estimatedHoursField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Horas Reales:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        actualHoursField = new JTextField(10);
        inputPanel.add(actualHoursField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Estado:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        statusComboBox = new JComboBox<>(Utils.getStatus());
        inputPanel.add(statusComboBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Agregar Incidente");
        addButton.addActionListener(e -> addIssue());
        inputPanel.add(addButton, gbc);

        // Deshabilitar los botones si el usuario no es admin
        if (!UserContext.getInstance().isAdminRole() && !UserContext.getInstance().isReporterRole()) {
            descriptionField.setEnabled(false);
            estimatedHoursField.setEnabled(false);
            actualHoursField.setEnabled(false);
            statusComboBox.setEnabled(false);
            addButton.setEnabled(false);
        }

        return inputPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Lista de Incidentes"));

        issueTableModel = new IssueTableModel(issueServiceImpl.getIssuesByProjectId(projectId));
        issueTable = new JTable(issueTableModel);
        JScrollPane scrollPane = new JScrollPane(issueTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        issueTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateHistoryButtonState();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton editButton = new JButton("Editar Incidente");
        editButton.addActionListener(e -> editIssue());
        buttonPanel.add(editButton);

        JButton closedButton = new JButton("Cerrar Incidente");
        closedButton.addActionListener(e -> closeIssue());
        buttonPanel.add(closedButton);

        if(!UserContext.getInstance().isAdminRole() && !UserContext.getInstance().isCloserRole()){
            closedButton.setVisible(false);
        }

        historyButton = new JButton("Ver Historial");
        historyButton.addActionListener(e -> viewHistory());
        buttonPanel.add(historyButton);

        JButton closeButton = new JButton("Volver");
        closeButton.addActionListener(e -> closeWindow());
        buttonPanel.add(closeButton);

        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private void viewHistory() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow >= 0) {
            Issue selectedIssue = issueTableModel.getIssueAt(selectedRow);
            new IssueHistoryFrame(selectedIssue.getId()).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un incidente primero.", "No se Seleccionó Incidente", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addIssue() {
        try {
            String description = descriptionField.getText();
            double estimatedHours = Double.parseDouble(estimatedHoursField.getText());
            double actualHours = Double.parseDouble(actualHoursField.getText());
            String status = (String) statusComboBox.getSelectedItem();

            Issue issue = new Issue();
            issue.setProjectId(projectId); // Establecer ID del proyecto
            issue.setDescription(description);
            issue.setEstimatedHours(estimatedHours);
            issue.setActualHours(actualHours);
            issue.setStatus(status);

            issueServiceImpl.createIssue(issue);
            descriptionField.setText("");
            estimatedHoursField.setText("");
            actualHoursField.setText("");

            loadIssues();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese números válidos para las horas estimadas y reales.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al agregar el incidente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editIssue() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow >= 0) {
            Issue selectedIssue = issueTableModel.getIssueAt(selectedRow);
            Issue oldIssue = new Issue(); // Crear una copia del incidente anterior
            oldIssue.setDescription(selectedIssue.getDescription());
            oldIssue.setEstimatedHours(selectedIssue.getEstimatedHours());
            oldIssue.setActualHours(selectedIssue.getActualHours());
            oldIssue.setStatus(selectedIssue.getStatus());

            try {
                User currentUser = UserContext.getInstance().getCurrentUser();

                String description = selectedIssue.getDescription();
                double estimatedHours = selectedIssue.getEstimatedHours();
                double actualHours = selectedIssue.getActualHours();
                String status = selectedIssue.getStatus();

                if (UserContext.getInstance().isAdminRole() || UserContext.getInstance().isReporterRole()) {
                    String newDescription = JOptionPane.showInputDialog(this, "Ingrese la nueva descripción:", selectedIssue.getDescription());
                    if (newDescription == null) return; // Abort if Cancel is pressed
                    description = newDescription;
                }
                if (UserContext.getInstance().isAdminRole() || UserContext.getInstance().isStatusRole()) {
                    String newStatus = (String) JOptionPane.showInputDialog(this, "Seleccione el nuevo estado:", "Estado", JOptionPane.QUESTION_MESSAGE, null, Utils.getStatus(), selectedIssue.getStatus());
                    if (newStatus == null) return; // Abort if Cancel is pressed
                    status = newStatus;
                }
                if (UserContext.getInstance().isAdminRole() || UserContext.getInstance().isTimerRole()) {
                    String newEstimatedHours = JOptionPane.showInputDialog(this, "Ingrese las nuevas horas estimadas:", selectedIssue.getEstimatedHours());
                    if (newEstimatedHours == null) return; // Abort if Cancel is pressed
                    estimatedHours = Double.parseDouble(newEstimatedHours);

                    String newActualHours = JOptionPane.showInputDialog(this, "Ingrese las nuevas horas reales:", selectedIssue.getActualHours());
                    if (newActualHours == null) return; // Abort if Cancel is pressed
                    actualHours = Double.parseDouble(newActualHours);
                }

                boolean changed = false;
                if (!description.equals(oldIssue.getDescription())) {
                    recordHistory(selectedIssue.getId(), currentUser, "Descripción", oldIssue.getDescription(), description);
                    selectedIssue.setDescription(description);
                    changed = true;
                }
                if (estimatedHours != oldIssue.getEstimatedHours()) {
                    recordHistory(selectedIssue.getId(), currentUser, "Horas Estimadas", String.valueOf(oldIssue.getEstimatedHours()), String.valueOf(estimatedHours));
                    selectedIssue.setEstimatedHours(estimatedHours);
                    changed = true;
                }
                if (actualHours != oldIssue.getActualHours()) {
                    recordHistory(selectedIssue.getId(), currentUser, "Horas Reales", String.valueOf(oldIssue.getActualHours()), String.valueOf(actualHours));
                    selectedIssue.setActualHours(actualHours);
                    changed = true;
                }
                if (!status.equals(oldIssue.getStatus())) {
                    recordHistory(selectedIssue.getId(), currentUser, "Estado", oldIssue.getStatus(), status);
                    selectedIssue.setStatus(status);
                    changed = true;
                }

                if (changed) {
                    issueServiceImpl.updateIssue(selectedIssue);
                    loadIssues();
                } else {
                    JOptionPane.showMessageDialog(this, "No se realizaron cambios.", "Información", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese números válidos para las horas estimadas y reales.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ocurrió un error al editar el incidente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void recordHistory(Long issueId, User currentUser, String field, String before, String after) {
        IssueHistory history = new IssueHistory();
        history.setIssueId(issueId);
        history.setUsername(currentUser.getUsername());
        history.setDate(new Date());
        history.setInfoBefore(field + ": " + before);
        history.setInfoAfter(field + ": " + after);
        new IssueHistoryService().createIssueHistory(history);
    }


    private void closeIssue() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow >= 0) {
            Issue selectedIssue = issueTableModel.getIssueAt(selectedRow);
            if (Utils.CLOSED_STATUS.equals(selectedIssue.getStatus())) {
                JOptionPane.showMessageDialog(this, "No es posible cerrar un proyecto en este estado", "Advertencia", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea cerrar este incidente?", "Confirmar cierre", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Registrar el historial de modificaciones antes de cerrar el incidente
                    try {
                        User currentUser = UserContext.getInstance().getCurrentUser();
                        recordHistory(selectedIssue.getId(), currentUser, "Estado", selectedIssue.getStatus(), Utils.CLOSED_STATUS);
                        // Cerrar el incidente
                        issueServiceImpl.closeIssue(selectedIssue.getId());
                        loadIssues();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Ocurrió un error al registrar el historial de cambios o cerrar el incidente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }


    private void closeWindow() {
        this.dispose();
    }

    private void loadIssues() {
        List<Issue> issues = issueServiceImpl.getIssuesByProjectId(projectId);
        issueTableModel.setIssues(issues);
        updateHistoryButtonState();
    }

    private void updateHistoryButtonState() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow >= 0) {
            Issue selectedIssue = issueTableModel.getIssueAt(selectedRow);
            List<IssueHistory> history = new IssueHistoryService().getIssueHistoryByIssueId(selectedIssue.getId());
            historyButton.setEnabled(history != null && !history.isEmpty());
        } else {
            historyButton.setEnabled(false);
        }
    }

    // Solamente para hacer test de cómo va quedando la pantalla
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager.createTables();
            new IssueManagementApp(1L).setVisible(true);
        });
    }
}
