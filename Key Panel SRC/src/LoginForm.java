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
            String query = "SELECT password, is_admin FROM users WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    boolean isAdmin = rs.getBoolean("is_admin");

                    if (password.equals(storedPassword)) {
                        if (isAdmin) {
                            JOptionPane.showMessageDialog(this, "Login successful! Logged in as Admin.", "Success", JOptionPane.INFORMATION_MESSAGE);

                        } else {
                            JOptionPane.showMessageDialog(this, "Login successful! Logged in as User.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            new UserForm().setVisible(true); // Open Register Form
                            dispose();
                        }
                        // Open the main application window or perform other actions
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