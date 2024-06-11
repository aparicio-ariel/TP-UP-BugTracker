package ui;

import database.DatabaseManager;
import model.IssueHistory;
import model.User;
import model.UserContext;
import service.IssueHistoryService;
import service.IssueService;
import model.Issue;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class IssueManagementApp extends JFrame {

    private IssueService issueService;
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
        issueService = new IssueService();

        setTitle("Gestión de Incidentes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTitleLabel(), BorderLayout.NORTH);
        add(createSplitPane(), BorderLayout.CENTER);

        loadIssues();
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("Gestión de Incidentes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return titleLabel;
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
        statusComboBox = new JComboBox<>(new String[]{"Abierto", "En Progreso", "Cerrado"});
        inputPanel.add(statusComboBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Agregar Incidente");
        addButton.addActionListener(e -> addIssue());
        inputPanel.add(addButton, gbc);

        // Deshabilitar los botones si el usuario no es admin
        if (!UserContext.getInstance().getCurrentUser().getRole().equals("admin") && !UserContext.getInstance().getCurrentUser().getRole().equals("reporter")) {
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

        issueTableModel = new IssueTableModel(issueService.getIssuesByProjectId(projectId));
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

        JButton deleteButton = new JButton("Cerrar Incidente");
        deleteButton.addActionListener(e -> closeIssue());
        buttonPanel.add(deleteButton);

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

            issueService.createIssue(issue);
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

                if (currentUser.getRole().equals("admin") || currentUser.getRole().equals("reporter")) {
                    String newDescription = JOptionPane.showInputDialog(this, "Ingrese la nueva descripción:", selectedIssue.getDescription());
                    if (newDescription == null) return; // Abort if Cancel is pressed
                    description = newDescription;
                }
                if (currentUser.getRole().equals("admin") || currentUser.getRole().equals("status_changer")) {
                    String newStatus = (String) JOptionPane.showInputDialog(this, "Seleccione el nuevo estado:", "Estado", JOptionPane.QUESTION_MESSAGE, null, new String[]{"Abierto", "En Progreso", "Cerrado"}, selectedIssue.getStatus());
                    if (newStatus == null) return; // Abort if Cancel is pressed
                    status = newStatus;
                }
                if (currentUser.getRole().equals("admin") || currentUser.getRole().equals("time_tracker")) {
                    String newEstimatedHours = JOptionPane.showInputDialog(this, "Ingrese las nuevas horas estimadas:", selectedIssue.getEstimatedHours());
                    if (newEstimatedHours == null) return; // Abort if Cancel is pressed
                    estimatedHours = Double.parseDouble(newEstimatedHours);

                    String newActualHours = JOptionPane.showInputDialog(this, "Ingrese las nuevas horas reales:", selectedIssue.getActualHours());
                    if (newActualHours == null) return; // Abort if Cancel is pressed
                    actualHours = Double.parseDouble(newActualHours);
                }

                selectedIssue.setDescription(description);
                selectedIssue.setEstimatedHours(estimatedHours);
                selectedIssue.setActualHours(actualHours);
                selectedIssue.setStatus(status);

                issueService.updateIssue(selectedIssue);

                // Registrar el historial de modificaciones
                IssueHistory history = new IssueHistory();
                history.setIssueId(selectedIssue.getId());
                history.setUsername(currentUser.getUsername());
                history.setDate(new Date());
                history.setInfoBefore("Descripción: " + oldIssue.getDescription() + ", Horas Estimadas: " + oldIssue.getEstimatedHours() + ", Horas Reales: " + oldIssue.getActualHours() + ", Estado: " + oldIssue.getStatus());
                history.setInfoAfter("Descripción: " + selectedIssue.getDescription() + ", Horas Estimadas: " + selectedIssue.getEstimatedHours() + ", Horas Reales: " + selectedIssue.getActualHours() + ", Estado: " + selectedIssue.getStatus());

                new IssueHistoryService().createIssueHistory(history);

                loadIssues();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese números válidos para las horas estimadas y reales.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ocurrió un error al editar el incidente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void closeIssue() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow >= 0) {
            Issue selectedIssue = issueTableModel.getIssueAt(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea cerrar este incidente?", "Confirmar cierre", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                issueService.closeIssue(selectedIssue.getId());
                loadIssues();
            }
        }
    }

    private void closeWindow() {
        this.dispose();
    }

    private void loadIssues() {
        List<Issue> issues = issueService.getIssuesByProjectId(projectId);
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
