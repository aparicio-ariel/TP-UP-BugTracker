package service;

import model.IssueHistory;
import database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class IssueHistoryService {

    public void createIssueHistory(IssueHistory history) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO IssueHistory (issue_id, username, date, info_before, info_after) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, history.getIssueId());
            pstmt.setString(2, history.getUsername());
            pstmt.setTimestamp(3, new java.sql.Timestamp(history.getDate().getTime()));
            pstmt.setString(4, history.getInfoBefore());
            pstmt.setString(5, history.getInfoAfter());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<IssueHistory> getIssueHistoryByIssueId(Long issueId) {
        List<IssueHistory> historyList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM IssueHistory WHERE issue_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, issueId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                IssueHistory history = new IssueHistory();
                history.setId(rs.getLong("id"));
                history.setIssueId(rs.getLong("issue_id"));
                history.setUsername(rs.getString("username"));
                history.setDate(rs.getTimestamp("date"));
                history.setInfoBefore(rs.getString("info_before"));
                history.setInfoAfter(rs.getString("info_after"));
                historyList.add(history);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historyList;
    }

    public List<IssueHistory> getAllIssueHistories() {
        List<IssueHistory> historyList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM IssueHistory";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                IssueHistory history = new IssueHistory();
                history.setId(rs.getLong("id"));
                history.setIssueId(rs.getLong("issue_id"));
                history.setUsername(rs.getString("username"));
                history.setDate(rs.getTimestamp("date"));
                history.setInfoBefore(rs.getString("info_before"));
                history.setInfoAfter(rs.getString("info_after"));
                historyList.add(history);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historyList;
    }
}
