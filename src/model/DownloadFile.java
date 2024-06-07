package model;

public class DownloadFile {
    private String projectName;
    private String description;
    private long countIssues;
    private double actualHours;
    private String status;

    // Getters y Setters
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getCountIssues() { return countIssues; }
    public void setCountIssues(long countIssues) { this.countIssues = countIssues; }
    public double getActualHours() { return actualHours; }
    public void setActualHours(double actualHours) { this.actualHours = actualHours; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
