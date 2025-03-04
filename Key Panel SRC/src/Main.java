import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    public Main() {
        setTitle("Key Panel");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set a modern color scheme
        Color backgroundColor = new Color(47, 49, 54);
        Color buttonColor = new Color(53, 59, 63);
        Color buttonTextColor = new Color(255, 255, 255);
        Color inputBackground = new Color(64, 68, 75);
        Color inputTextColor = new Color(255, 255, 255);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(backgroundColor);

        // Create a title label
        JLabel titleLabel = new JLabel("Welcome to Key Panel", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Centered input fields panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 1, 10, 10));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Username field
        JTextField usernameField = new JTextField();
        usernameField.setBackground(inputBackground);
        usernameField.setForeground(inputTextColor);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonColor, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));

        // Password field
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBackground(inputBackground);
        passwordField.setForeground(inputTextColor);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonColor, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 10));
        buttonPanel.setBackground(backgroundColor);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(buttonColor);
        loginButton.setForeground(buttonTextColor);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(buttonColor);
        registerButton.setForeground(buttonTextColor);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // Adding components to the form panel
        formPanel.add(usernameField);
        formPanel.add(passwordField);
        formPanel.add(buttonPanel);

        // Adding all components to the main panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main form = new Main();
            form.setVisible(true);
        });
    }
}
