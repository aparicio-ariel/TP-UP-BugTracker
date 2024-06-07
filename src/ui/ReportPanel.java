package ui;

import model.ProjectReport;
import model.DownloadFile;
import model.UserContext;
import service.ReportService;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportPanel extends JPanel {

    private ReportService reportService;
    private JTable reportTable;
    private ProjectReportTableModel tableModel;

    public ReportPanel() {
        reportService = new ReportService();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Project Report"));

        tableModel = new ProjectReportTableModel(List.of());
        reportTable = new JTable(tableModel);
        reportTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());  // Aplicar el renderizador solo a la columna de estado

        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadReportData());
        bottomPanel.add(refreshButton);

        JButton downloadButton = new JButton("Descargar Reporte");
        downloadButton.addActionListener(e -> downloadReport());
        bottomPanel.add(downloadButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Deshabilitar los botones si el usuario no es admin
        if (!UserContext.getInstance().getCurrentUser().getRole().equals("admin")) {
            downloadButton.setEnabled(false);
        }
    }

    public void loadReportData() {
        List<ProjectReport> reports = reportService.getProjectReports();
        tableModel.setReports(reports);
    }

    private void downloadReport() {
        List<DownloadFile> downloadFiles = reportService.getDownloadFileData();

        try (FileWriter writer = new FileWriter("project_report.csv")) {
            writer.append("Project Name, Description, Issue Count, Actual Hours, Status\n");
            for (DownloadFile df : downloadFiles) {
                writer.append(df.getProjectName())
                        .append(", ")
                        .append(df.getDescription())
                        .append(", ")
                        .append(String.valueOf(df.getCountIssues()))
                        .append(", ")
                        .append(String.valueOf(df.getActualHours()))
                        .append(", ")
                        .append(df.getStatus())
                        .append("\n");
            }
            JOptionPane.showMessageDialog(this, "Reporte descargado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al descargar el reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
