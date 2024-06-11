package ui;

import model.Issue;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class IssueTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Descripción", "Horas Estimadas", "Horas Reales", "Estado"};
    private List<Issue> issues;

    public IssueTableModel(List<Issue> issues) {
        this.issues = issues;
    }

    @Override
    public int getRowCount() {
        return issues.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Issue issue = issues.get(rowIndex);
        switch (columnIndex) {
            case 0: return issue.getDescription();
            case 1: return issue.getEstimatedHours();
            case 2: return issue.getActualHours();
            case 3: return issue.getStatus();
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public Issue getIssueAt(int rowIndex) {
        return issues.get(rowIndex);
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
        fireTableDataChanged();
    }
}
