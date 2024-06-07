package model;

import java.util.Date;

public class IssueHistory {
    private Long id;
    private Long issueId;
    private String username;
    private Date date;
    private String infoBefore;
    private String infoAfter;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getInfoBefore() {
        return infoBefore;
    }

    public void setInfoBefore(String infoBefore) {
        this.infoBefore = infoBefore;
    }

    public String getInfoAfter() {
        return infoAfter;
    }

    public void setInfoAfter(String infoAfter) {
        this.infoAfter = infoAfter;
    }
}
