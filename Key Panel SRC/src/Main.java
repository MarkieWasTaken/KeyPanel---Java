import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Initialize the login form
            new LoginForm().setVisible(true);

            // Check database connection after starting the form
            checkDatabaseConnection();
        });
    }

    private static void checkDatabaseConnection() {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/keypanel"; // Replace with your database URL
        String user = "root"; // Replace with your database username
        String password = ""; // Replace with your database password

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            if (connection != null) {
                System.out.println("Database Connection: Valid");
            } else {
                System.out.println("Database Connection: Invalid");
            }
        } catch (SQLException e) {
            System.out.println("Database Connection: Invalid");
            e.printStackTrace();
        }
    }
}
