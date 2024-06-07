package service;

import database.DatabaseManager;
import model.DownloadFile;
import model.ProjectReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    public List<ProjectReport> getProjectReports() {
        List<ProjectReport> reports = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT p.name AS project_name, SUM(i.estimated_hours) AS estimated_hours, SUM(i.actual_hours) AS actual_hours " +
                    "FROM Projects p " +
                    "JOIN Issues i ON p.id = i.project_id " +
                    "GROUP BY p.name";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ProjectReport report = new ProjectReport();
                report.setProjectName(rs.getString("project_name"));
                report.setEstimatedHours(rs.getDouble("estimated_hours"));
                report.setActualHours(rs.getDouble("actual_hours"));
                reports.add(report);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reports;
    }

    public List<DownloadFile> getDownloadFileData() {
        List<DownloadFile> downloadFiles = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "select p.name as name, p.description as description, count(i.*) as countIssues, " +
                    "sum(i.actual_hours) as actual_hours, i.status as status " +
                    "from projects p " +
                    "left join issues i on i.project_id = p.id " +
                    "group by p.name, i.status";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                DownloadFile downloadFile = new DownloadFile();
                downloadFile.setProjectName(rs.getString("name"));
                downloadFile.setDescription(rs.getString("description"));
                downloadFile.setCountIssues(rs.getLong("countIssues"));
                downloadFile.setActualHours(rs.getDouble("actual_hours"));
                downloadFile.setStatus(rs.getString("status"));
                downloadFiles.add(downloadFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return downloadFiles;
    }
}
