package ui;

import model.ProjectReport;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ProjectReportTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Nombre del Proyecto", "Horas Estimadas", "Horas Reales", "Estado"};
    private List<ProjectReport> reports;

    public ProjectReportTableModel(List<ProjectReport> reports) {
        this.reports = reports;
    }

    @Override
    public int getRowCount() {
        return reports.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ProjectReport report = reports.get(rowIndex);
        switch (columnIndex) {
            case 0: return report.getProjectName();
            case 1: return report.getEstimatedHours();
            case 2: return report.getActualHours();
            case 3: return report.getActualHours() > report.getEstimatedHours() ? "Retrasado" : "Adelantado";
            default: return null;
        }
    }

    public void setReports(List<ProjectReport> reports) {
        this.reports = reports;
        fireTableDataChanged();
    }
}
