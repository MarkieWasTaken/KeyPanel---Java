import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JFrame {
    public RegisterForm() {
        setTitle("Register Panel");
        setSize(400, 650);  // Increase size to accommodate new field
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

        // Style the labels
        styleLabel(usernameLabel);
        styleLabel(emailLabel);
        styleLabel(passwordLabel);
        styleLabel(locationLabel);

        // Text Fields and ComboBox
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        // Create the Location dropdown (ComboBox)
        String[] locations = {"New York", "Los Angeles", "Chicago", "Miami", "Houston"};
        JComboBox<String> locationComboBox = new JComboBox<>(locations);
        locationComboBox.setPreferredSize(new Dimension(300, 40));
        locationComboBox.setBackground(new Color(64, 68, 75));
        locationComboBox.setForeground(Color.WHITE);
        locationComboBox.setFont(new Font("Arial", Font.PLAIN, 16));

        // Style text fields
        styleTextField(usernameField);
        styleTextField(emailField);
        styleTextField(passwordField);

        // Buttons
        JButton registerButton = createStyledButton("Register");
        JButton backButton = createStyledButton("Back to Login");

        // Add components to the formPanel
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
        formPanel.add(locationLabel, gbc);  // Add the Location label
        gbc.gridy++;
        formPanel.add(locationComboBox, gbc);  // Add the Location ComboBox
        gbc.gridy++;
        formPanel.add(registerButton, gbc);
        gbc.gridy++;
        formPanel.add(backButton, gbc);

        // Add everything to the panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        add(panel);
    }

    // Method to style text fields
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

    // Method to style labels
    private void styleLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
    }

    // Method to create styled buttons
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
