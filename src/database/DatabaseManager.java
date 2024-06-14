package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String JDBC_URL = "jdbc:h2:~/test;DB_CLOSE_DELAY=-1";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    public static void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(255) NOT NULL UNIQUE," +
                    "password VARCHAR(255) NOT NULL," +
                    "role VARCHAR(50) NOT NULL" +
                    ")";
            stmt.execute(createUsersTable);

            String createProjectsTable = "CREATE TABLE IF NOT EXISTS Projects (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "description TEXT" +
                    ")";
            stmt.execute(createProjectsTable);

            String createIssuesTable = "CREATE TABLE IF NOT EXISTS Issues (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "project_id BIGINT," +  // Referencia al proyecto
                    "description TEXT NOT NULL," +
                    "estimated_hours DOUBLE," +
                    "actual_hours DOUBLE," +
                    "status VARCHAR(50)," +
                    "FOREIGN KEY (project_id) REFERENCES Projects(id)" +
                    ")";
            stmt.execute(createIssuesTable);

            String createIssuesHistoryTable = "CREATE TABLE IF NOT EXISTS IssueHistory (" +
                    "id IDENTITY PRIMARY KEY, " +
                    "issue_id BIGINT, " +
                    "username VARCHAR(255), " +
                    "date TIMESTAMP, " +
                    "info_before CLOB, " +
                    "info_after CLOB, " +
                    "FOREIGN KEY (issue_id) REFERENCES Issues(id)" +
                    ")";
            stmt.execute(createIssuesHistoryTable);

            System.out.println("Tables created successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
