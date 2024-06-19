package interfaces;

import model.Issue;
import java.util.List;

public interface IssueService {
    List<Issue> getAllIssues();
    void deleteIssuesByProjectId(Long projectId);
    List<Issue> getIssuesByProjectId(Long projectId);
    void createIssue(Issue issue);
    void updateIssue(Issue issue);
    void deleteIssue(Long issueId);
    void closeIssue(Long issueId);
}
