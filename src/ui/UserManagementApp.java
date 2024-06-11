package ui;

import model.IssueHistory;
import model.UserContext;
import service.IssueHistoryService;
import service.IssueService;
import service.ProjectService;
import service.UserService;
import model.Project;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class UserManagementApp extends JFrame {

    private UserService userService;
    private ProjectService projectService;
    private IssueService issueService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JList<User> userList;
    private JTextField projectNameField;
    private JTextArea projectDescriptionArea;
    private JList<Project> projectList;
    private User currentUser;

    public UserManagementApp() {
        this.currentUser = UserContext.getInstance().getCurrentUser();
        userService = new UserService();
        projectService = new ProjectService();
        issueService = new IssueService();

        setTitle("Management");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("Project Management", createProjectPanel());
        if (currentUser.getRole().equals("admin")) {
            tabbedPane.addTab("Issue History", createIssueHistoryPanel());
            tabbedPane.addTab("User Management", createUserPanel());
        }
        tabbedPane.addTab("Project Report", createReportPanel());

        loadUsers();
        loadProjects();
    }

    private JPanel createReportPanel() {
        return new ReportPanel();
    }

    private void addUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password fields cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        try {
            userService.createUser(user);
            loadUsers();
        } catch (Exception e) {
            if (e.getMessage().contains("Unique index or primary key violation")) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another username.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "An error occurred while adding the user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editUser() {
        User selectedUser = userList.getSelectedValue();
        if (selectedUser != null) {
            try {
                String username = JOptionPane.showInputDialog(this, "Enter new username:", selectedUser.getUsername());
                if (username == null) return; // Abort if Cancel is pressed

                String password = JOptionPane.showInputDialog(this, "Enter new password:", selectedUser.getPassword());
                if (password == null) return; // Abort if Cancel is pressed

                String role = (String) JOptionPane.showInputDialog(this, "Select new role:", "Role",
                        JOptionPane.QUESTION_MESSAGE, null, new String[]{"admin", "reporter", "status_change", "time_tracker", "closer"}, selectedUser.getRole());
                if (role == null) return; // Abort if Cancel is pressed

                selectedUser.setUsername(username);
                selectedUser.setPassword(password);
                selectedUser.setRole(role);
                userService.updateUser(selectedUser);
                loadUsers();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "An error occurred while editing the user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteUser() {
        User selectedUser = userList.getSelectedValue();
        if (selectedUser != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    userService.deleteUser(selectedUser.getId());
                    loadUsers();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "An error occurred while deleting the user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void loadUsers() {
        if (currentUser.getRole().equals("admin")) {
            List<User> users = userService.getAllUsers();
            DefaultListModel<User> model = new DefaultListModel<>();
            for (User user : users) {
                model.addElement(user);
            }
            userList.setModel(model);
        }
    }

    private void addProject() {
        if (!currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(this, "Only admin users can add projects.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = projectNameField.getText().trim();
        String description = projectDescriptionArea.getText().trim();

        if (name.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Project name and description cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Project project = new Project();
        project.setName(name);
        project.setDescription(description);

        try {
            projectService.createProject(project);
            loadProjects();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred while adding the project: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editProject() {
        Project selectedProject = projectList.getSelectedValue();
        if (selectedProject != null) {
            try {
                String name = JOptionPane.showInputDialog(this, "Enter new project name:", selectedProject.getName());
                if (name == null) return; // Abort if Cancel is pressed

                String description = JOptionPane.showInputDialog(this, "Enter new project description:", selectedProject.getDescription());
                if (description == null) return; // Abort if Cancel is pressed

                selectedProject.setName(name);
                selectedProject.setDescription(description);
                projectService.updateProject(selectedProject);
                loadProjects();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "An error occurred while editing the project: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteProject() {
        Project selectedProject = projectList.getSelectedValue();
        if (selectedProject != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this project?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    issueService.deleteIssuesByProjectId(selectedProject.getId()); // Delete associated issues first
                    projectService.deleteProject(selectedProject.getId());
                    loadProjects();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "An error occurred while deleting the project: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void loadProjects() {
        List<Project> projects = projectService.getAllProjects();
        DefaultListModel<Project> model = new DefaultListModel<>();
        for (Project project : projects) {
            model.addElement(project);
        }
        projectList.setModel(model);
    }

    private JPanel createUserPanel() {
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("User Information"));

        GridBagConstraints gbcUser = new GridBagConstraints();
        gbcUser.insets = new Insets(10, 10, 10, 10);

        addLabelAndField(userPanel, gbcUser, "Username:", usernameField = new JTextField(20), 0);
        addLabelAndField(userPanel, gbcUser, "Password:", passwordField = new JPasswordField(20), 1);
        addLabelAndField(userPanel, gbcUser, "Role:", roleComboBox = new JComboBox<>(new String[]{"admin", "REPORTER", "STATUS_CHANGER", "TIME_TRACKER", "CLOSER"}), 2);

        gbcUser.gridx = 1;
        gbcUser.gridy = 3;
        gbcUser.anchor = GridBagConstraints.CENTER;
        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(e -> addUser());
        userPanel.add(addUserButton, gbcUser);

        JPanel userListPanel = new JPanel(new BorderLayout());
        userListPanel.setBorder(BorderFactory.createTitledBorder("User List"));
        gbcUser.gridx = 0;
        gbcUser.gridy = 4;
        gbcUser.gridwidth = 2;
        userPanel.add(userListPanel, gbcUser);

        userList = new JList<>();
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userListPanel.add(userScrollPane, BorderLayout.CENTER);

        JPanel userButtonPanel = new JPanel(new FlowLayout());
        addUserManagementButtons(userButtonPanel);
        userListPanel.add(userButtonPanel, BorderLayout.SOUTH);

        return userPanel;
    }

    private void addUserManagementButtons(JPanel panel) {
        JButton editUserButton = new JButton("Edit User");
        editUserButton.addActionListener(e -> editUser());
        panel.add(editUserButton);

        JButton deleteUserButton = new JButton("Delete User");
        deleteUserButton.addActionListener(e -> deleteUser());
        panel.add(deleteUserButton);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, Component field, int yPosition) {
        gbc.gridx = 0;
        gbc.gridy = yPosition;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.gridy = yPosition;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    private JPanel createProjectPanel() {
        JPanel projectPanel = new JPanel(new GridBagLayout());
        projectPanel.setBorder(BorderFactory.createTitledBorder("Project Information"));

        GridBagConstraints gbcProject = new GridBagConstraints();
        gbcProject.insets = new Insets(10, 10, 10, 10);

        addLabelAndField(projectPanel, gbcProject, "Project Name:", projectNameField = new JTextField(20), 0);
        addLabelAndField(projectPanel, gbcProject, "Description:", projectDescriptionArea = new JTextArea(5, 20), 1);

        gbcProject.gridx = 1;
        gbcProject.gridy = 2;
        gbcProject.anchor = GridBagConstraints.CENTER;
        JButton addProjectButton = new JButton("Add Project");
        addProjectButton.addActionListener(e -> addProject());
        projectPanel.add(addProjectButton, gbcProject);

        JPanel projectListPanel = new JPanel(new BorderLayout());
        projectListPanel.setBorder(BorderFactory.createTitledBorder("Project List"));
        gbcProject.gridx = 0;
        gbcProject.gridy = 3;
        gbcProject.gridwidth = 2;
        projectPanel.add(projectListPanel, gbcProject);

        projectList = new JList<>();
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane projectScrollPane = new JScrollPane(projectList);
        projectListPanel.add(projectScrollPane, BorderLayout.CENTER);

        JPanel projectButtonPanel = new JPanel(new FlowLayout());
        addProjectManagementButtons(projectButtonPanel);
        projectListPanel.add(projectButtonPanel, BorderLayout.SOUTH);

        JButton manageIssuesButton = new JButton("Manage Issues");
        manageIssuesButton.addActionListener(e -> manageIssues());
        gbcProject.gridy = 4;
        projectPanel.add(manageIssuesButton, gbcProject);

        disableNonAdminControls(addProjectButton, manageIssuesButton);

        return projectPanel;
    }

    private void addProjectManagementButtons(JPanel panel) {
        JButton editProjectButton = new JButton("Edit Project");
        editProjectButton.addActionListener(e -> editProject());
        panel.add(editProjectButton);

        JButton deleteProjectButton = new JButton("Delete Project");
        deleteProjectButton.addActionListener(e -> deleteProject());
        panel.add(deleteProjectButton);
    }

    private void manageIssues() {
        Project selectedProject = projectList.getSelectedValue();
        if (selectedProject != null) {
            new IssueManagementApp(selectedProject.getId()).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a project first.", "No Project Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void disableNonAdminControls(JButton addProjectButton, JButton manageIssuesButton) {
        if (!currentUser.getRole().equals("admin")) {
            addProjectButton.setEnabled(false);
            manageIssuesButton.setEnabled(false);
            projectNameField.setEnabled(false);
            projectDescriptionArea.setEnabled(false);
        }
    }

    private JPanel createIssueHistoryPanel() {
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Issue History"));

        JList<IssueHistory> historyList = new JList<>();
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane historyScrollPane = new JScrollPane(historyList);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        loadIssueHistory(historyList);

        return historyPanel;
    }

    private void loadIssueHistory(JList<IssueHistory> historyList) {
        IssueHistoryService issueHistoryService = new IssueHistoryService();
        List<IssueHistory> histories = issueHistoryService.getAllIssueHistories();
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
                    setText("<html><b>User:</b> " + history.getUsername() +
                            "<br><b>Date:</b> " + history.getDate() +
                            "<br><b>Before:</b> " + history.getInfoBefore() +
                            "<br><b>After:</b> " + history.getInfoAfter() + "<hr></html>");
                }
                return this;
            }
        });
    }
}
