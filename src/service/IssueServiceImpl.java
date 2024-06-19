package service;

import database.DatabaseManager;
import interfaces.IssueService;
import model.Issue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueServiceImpl implements IssueService {

    @Override
    public List<Issue> getAllIssues() {
        List<Issue> issues = new ArrayList<>();
        String query = "SELECT * FROM Issues";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Issue issue = new Issue();
                issue.setId(rs.getLong("id"));
                issue.setProjectId(rs.getLong("project_id"));
                issue.setDescription(rs.getString("description"));
                issue.setEstimatedHours(rs.getDouble("estimated_hours"));
                issue.setActualHours(rs.getDouble("actual_hours"));
                issue.setStatus(rs.getString("status"));
                issues.add(issue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return issues;
    }

    @Override
    public void deleteIssuesByProjectId(Long projectId) {
        String deleteIssueHistoryQuery = "DELETE FROM IssueHistory WHERE issue_id IN (SELECT id FROM Issues WHERE project_id = ?)";
        String deleteIssuesQuery = "DELETE FROM Issues WHERE project_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement deleteHistoryStmt = conn.prepareStatement(deleteIssueHistoryQuery);
             PreparedStatement deleteIssuesStmt = conn.prepareStatement(deleteIssuesQuery)) {

            conn.setAutoCommit(false);  // Begin transaction

            deleteHistoryStmt.setLong(1, projectId);
            deleteHistoryStmt.executeUpdate();

            deleteIssuesStmt.setLong(1, projectId);
            deleteIssuesStmt.executeUpdate();

            conn.commit();  // Commit transaction
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Issue> getIssuesByProjectId(Long projectId) {
        List<Issue> issues = new ArrayList<>();
        String query = "SELECT * FROM Issues WHERE project_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Issue issue = new Issue();
                issue.setId(rs.getLong("id"));
                issue.setProjectId(rs.getLong("project_id"));
                issue.setDescription(rs.getString("description"));
                issue.setEstimatedHours(rs.getDouble("estimated_hours"));
                issue.setActualHours(rs.getDouble("actual_hours"));
                issue.setStatus(rs.getString("status"));
                issues.add(issue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return issues;
    }

    @Override
    public void createIssue(Issue issue) {
        String query = "INSERT INTO Issues (project_id, description, estimated_hours, actual_hours, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, issue.getProjectId());
            pstmt.setString(2, issue.getDescription());
            pstmt.setDouble(3, issue.getEstimatedHours());
            pstmt.setDouble(4, issue.getActualHours());
            pstmt.setString(5, issue.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateIssue(Issue issue) {
        String query = "UPDATE Issues SET description = ?, estimated_hours = ?, actual_hours = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, issue.getDescription());
            pstmt.setDouble(2, issue.getEstimatedHours());
            pstmt.setDouble(3, issue.getActualHours());
            pstmt.setString(4, issue.getStatus());
            pstmt.setLong(5, issue.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteIssue(Long issueId) {
        String query = "DELETE FROM Issues WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, issueId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeIssue(Long issueId) {
        String query = "UPDATE Issues SET status = 'Cerrado' WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, issueId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
