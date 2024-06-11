package ui;

import model.IssueHistory;
import service.IssueHistoryService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class IssueHistoryFrame extends JFrame {

    public IssueHistoryFrame(Long issueId) {
        setTitle("Historial del Incidente");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JList<IssueHistory> historyList = new JList<>();
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(historyList);
        add(scrollPane, BorderLayout.CENTER);

        loadIssueHistory(issueId, historyList);
    }

    private void loadIssueHistory(Long issueId, JList<IssueHistory> historyList) {
        IssueHistoryService issueHistoryService = new IssueHistoryService();
        List<IssueHistory> histories = issueHistoryService.getIssueHistoryByIssueId(issueId);
        DefaultListModel<IssueHistory> model = new DefaultListModel<>();
        for (IssueHistory history : histories) {
            model.addElement(history);
        }
        historyList.setModel(model);
        historyList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof IssueHistory) {
                    IssueHistory history = (IssueHistory) value;
                    setText("<html><b>Usuario:</b> " + history.getUsername() +
                            "<br><b>Fecha:</b> " + history.getDate() +
                            "<br><b>Antes:</b> " + history.getInfoBefore() +
                            "<br><b>Después:</b> " + history.getInfoAfter() + "<hr></html>");
                }
                return this;
            }
        });
    }
}
