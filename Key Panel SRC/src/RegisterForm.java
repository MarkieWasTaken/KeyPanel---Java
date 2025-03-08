import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Statement;

public class RegisterForm extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> locationComboBox;

    public RegisterForm() {
        setTitle("Register Panel");
        setSize(400, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(47, 49, 54));

        JLabel titleLabel = new JLabel("Create an Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(47, 49, 54));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;

        // Labels
        JLabel usernameLabel = new JLabel("Username:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel locationLabel = new JLabel("Location:");

        styleLabel(usernameLabel);
        styleLabel(emailLabel);
        styleLabel(passwordLabel);
        styleLabel(locationLabel);

        // Text Fields
        usernameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();

        // Fetch locations from database
        locationComboBox = new JComboBox<>(fetchLocationsFromDatabase());
        locationComboBox.setPreferredSize(new Dimension(300, 40));
        locationComboBox.setBackground(new Color(64, 68, 75));
        locationComboBox.setForeground(Color.WHITE);
        locationComboBox.setFont(new Font("Arial", Font.PLAIN, 16));

        styleTextField(usernameField);
        styleTextField(emailField);
        styleTextField(passwordField);

        // Buttons
        JButton registerButton = createStyledButton("Register");
        JButton backButton = createStyledButton("Back to Login");

        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridy++;
        formPanel.add(usernameField, gbc);
        gbc.gridy++;
        formPanel.add(emailLabel, gbc);
        gbc.gridy++;
        formPanel.add(emailField, gbc);
        gbc.gridy++;
        formPanel.add(passwordLabel, gbc);
        gbc.gridy++;
        formPanel.add(passwordField, gbc);
        gbc.gridy++;
        formPanel.add(locationLabel, gbc);
        gbc.gridy++;
        formPanel.add(locationComboBox, gbc);
        gbc.gridy++;
        formPanel.add(registerButton, gbc);
        gbc.gridy++;
        formPanel.add(backButton, gbc);

        // Add action listeners
        registerButton.addActionListener(e -> registerUser());
        backButton.addActionListener(e -> {
            new LoginForm().setVisible(true); // Open Login Form
            dispose(); // Close Register Form
        });

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        add(panel);
    }

    private void registerUser() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String location = (String) locationComboBox.getSelectedItem();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Check if username or email already exists
            String checkQuery = "SELECT id FROM users WHERE username = ? OR email = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, email);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Username or email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Insert new user
            String insertQuery = "INSERT INTO users (username, password, email, location_id, is_admin, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password); // Store password in plain text
                insertStmt.setString(3, email);
                insertStmt.setInt(4, getLocationId(location)); // Get location ID
                insertStmt.setBoolean(5, false); // Default to non-admin
                insertStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new LoginForm().setVisible(true); // Open Login Form
                dispose(); // Close Register Form
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getLocationId(String locationName) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT id FROM location WHERE name = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, locationName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1; // Location not found
    }

    private String[] fetchLocationsFromDatabase() {
        ArrayList<String> locations = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM location")) {

            while (rs.next()) {
                locations.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching locations: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return locations.toArray(new String[0]); // Convert list to array for JComboBox
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(300, 40));
        field.setBackground(new Color(64, 68, 75));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(53, 59, 63), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 16));
    }

    private void styleLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(300, 45));
        button.setBackground(new Color(53, 59, 63));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }
}