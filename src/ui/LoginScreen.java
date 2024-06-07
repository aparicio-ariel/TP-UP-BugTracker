package ui;

import database.DatabaseManager;
import model.UserContext;
import service.UserService;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {

    private UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font FOOTER_FONT = new Font("Arial", Font.ITALIC, 12);
    private static final Insets FIELD_INSETS = new Insets(5, 5, 5, 5);
    private static final Dimension RIGID_AREA_DIMENSION = new Dimension(0, 20);

    public LoginScreen() {
        userService = new UserService();
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = createMainPanel();
        add(mainPanel);

        JLabel titleLabel = createTitleLabel();
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createRigidArea(RIGID_AREA_DIMENSION));

        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel);

        mainPanel.add(Box.createRigidArea(RIGID_AREA_DIMENSION));

        JButton loginButton = createLoginButton();
        mainPanel.add(loginButton);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel footerLabel = createFooterLabel();
        mainPanel.add(footerLabel);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return mainPanel;
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("Issue Tracker Login");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return titleLabel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = FIELD_INSETS;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(15);
        inputPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(15);
        inputPanel.add(passwordField, gbc);

        return inputPanel;
    }

    private JButton createLoginButton() {
        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> login());
        return loginButton;
    }

    private JLabel createFooterLabel() {
        JLabel footerLabel = new JLabel("© 2024 Issue Tracker");
        footerLabel.setFont(FOOTER_FONT);
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return footerLabel;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password fields cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userService.getUserByUsernameAndPassword(username, password);
        if (user != null) {
            UserContext.getInstance().setCurrentUser(user);
            new UserManagementApp().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            clearFields();
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager.createTables();
            new LoginScreen().setVisible(true);
        });
    }
}
