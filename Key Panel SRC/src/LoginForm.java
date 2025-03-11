import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Login Panel");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(47, 49, 54));

        JLabel titleLabel = new JLabel("Welcome Back", JLabel.CENTER);
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

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        styleLabel(usernameLabel);
        styleLabel(passwordLabel);

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        styleTextField(usernameField);
        styleTextField(passwordField);

        JButton loginButton = createStyledButton("Login");
        JButton registerButton = createStyledButton("Create Account");

        // Add Action Listener for Login Button
        loginButton.addActionListener(e -> loginUser());

        // Add Action Listener for Register Button
        registerButton.addActionListener(e -> {
            new RegisterForm().setVisible(true); // Open Register Form
            dispose(); // Close Login Form
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridy++;
        formPanel.add(usernameField, gbc);
        gbc.gridy++;
        formPanel.add(passwordLabel, gbc);
        gbc.gridy++;
        formPanel.add(passwordField, gbc);
        gbc.gridy++;
        formPanel.add(loginButton, gbc);
        gbc.gridy++;
        formPanel.add(registerButton, gbc);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        add(panel);
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM check_user_credentials(?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    boolean isValid = rs.getBoolean("is_valid");
                    boolean isAdmin = rs.getBoolean("is_admin");

                    if (isValid) {
                        if (isAdmin) {
                            JOptionPane.showMessageDialog(this, "Login successful! Logged in as Admin.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            new AdminForm().setVisible(true);
                            dispose();
                        } else {
                            // Check if the user has a valid key
                            if (hasValidKey(username)) {
                                JOptionPane.showMessageDialog(this, "Login successful! Redirecting to UserForm.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                new UserForm(username).setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(this, "Login successful! Please activate your account.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                new AuthForm(username).setVisible(true);
                            }
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean hasValidKey(String username) {
        String query = "SELECT has_valid_license(?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1); // Use getBoolean instead of getInt
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Styling methods for text field and button
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
