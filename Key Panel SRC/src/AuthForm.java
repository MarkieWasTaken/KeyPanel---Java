import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AuthForm extends JFrame {
    private JTextField licenseField;
    private String username;

    public AuthForm(String username) {
        this.username = username;  // Store the username for later use
        setTitle("User Activation");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(47, 49, 54));

        JLabel titleLabel = new JLabel("Activate your account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
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

        licenseField = new JTextField();
        styleTextField(licenseField);

        JButton checkLicenseButton = createStyledButton("Check License");

        // Action listener for license check
        checkLicenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String licenseKey = licenseField.getText();
                if (licenseKey.isEmpty()) {
                    JOptionPane.showMessageDialog(AuthForm.this, "Please enter a license key.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Use the username that was passed from the LoginForm
                    checkLicenseKey(licenseKey, username);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(licenseField, gbc);
        gbc.gridy++;
        formPanel.add(checkLicenseButton, gbc);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        add(panel);
    }

    // Method to check the license key from the database
    private void checkLicenseKey(String licenseKey, String username) {
        String query = "SELECT * FROM check_license_key(?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, licenseKey);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // License is found and inactive
                int licenseId = rs.getInt("license_id");  // Correct column name "license_id"
                boolean isValid = rs.getBoolean("is_valid");  // Correct column name "is_valid"

                if (!isValid) {
                    // Step 2: Activate the license
                    String updateLicenseQuery = "SELECT activate_license(?)";
                    try (PreparedStatement updatePs = connection.prepareStatement(updateLicenseQuery)) {
                        updatePs.setInt(1, licenseId);
                        updatePs.execute();  // Use execute() instead of executeUpdate()
                    }


                    // Step 3: Get user ID by username
                    String userQuery = "SELECT get_user_id_by_username(?)";
                    try (PreparedStatement userPs = connection.prepareStatement(userQuery)) {
                        userPs.setString(1, username);
                        ResultSet userRs = userPs.executeQuery();

                        if (userRs.next()) {
                            int userId = userRs.getInt(1); // User ID returned by the function

                            // Step 4: Assign license to user
                            String assignLicenseQuery = "SELECT assign_license_to_user(?, ?)";
                            try (PreparedStatement assignPs = connection.prepareStatement(assignLicenseQuery)) {
                                assignPs.setInt(1, licenseId);
                                assignPs.setInt(2, userId);
                                assignPs.execute();  // Use execute() instead of executeUpdate()
                            }

                            JOptionPane.showMessageDialog(this, "License activated and assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            new UserForm(username).setVisible(true);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    // License key already activated or expired
                    JOptionPane.showMessageDialog(this, "Invalid, expired, or already activated license key.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
