import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminForm extends JFrame {
    private JComboBox<String> durationComboBox;
    private JTextField licenseKeyField;
    private JButton generateKeyButton;
    private JButton listUsersButton;
    private JButton goToListKeysButton;  // Added this button

    public AdminForm() {
        setTitle("Admin Panel");
        setSize(500, 500); // Increased height to accommodate the new button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(47, 49, 54));
        JLabel titleLabel = new JLabel("Admin Panel - License Key Generator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(47, 49, 54));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;

        JLabel durationLabel = new JLabel("Select Duration:");
        styleLabel(durationLabel);

        String[] durations = {"1 Month", "6 Months", "1 Year"};
        durationComboBox = new JComboBox<>(durations);
        styleComboBox(durationComboBox);

        JLabel keyLabel = new JLabel("License Key:");
        styleLabel(keyLabel);

        licenseKeyField = new JTextField();
        styleTextField(licenseKeyField);

        generateKeyButton = createStyledButton("Generate License Key");

        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateLicenseKey();
            }
        });

        listUsersButton = createStyledButton("Go to List Users");
        listUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ListUsersForm().setVisible(true);
                dispose();
            }
        });

        goToListKeysButton = createStyledButton("Go to List Keys");  // New button
        goToListKeysButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ListKeysForm().setVisible(true); // Open List Keys Form
                dispose(); // Close Admin Form
            }
        });

        // Adding components to formPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(durationLabel, gbc);
        gbc.gridy++;
        formPanel.add(durationComboBox, gbc);
        gbc.gridy++;
        formPanel.add(keyLabel, gbc);
        gbc.gridy++;
        formPanel.add(licenseKeyField, gbc);
        gbc.gridy++;
        formPanel.add(generateKeyButton, gbc);
        gbc.gridy++;
        formPanel.add(listUsersButton, gbc);
        gbc.gridy++;
        formPanel.add(goToListKeysButton, gbc);  // Add new button here

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        add(panel);
    }

    private void generateLicenseKey() {
        String durationText = (String) durationComboBox.getSelectedItem();
        int duration = 0;

        // Map duration text to number of months
        switch (durationText) {
            case "1 Month":
                duration = 1;
                break;
            case "6 Months":
                duration = 6;
                break;
            case "1 Year":
                duration = 12;
                break;
        }

        if (duration == 0) {
            JOptionPane.showMessageDialog(this, "Please select a valid duration.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String licenseKey = generateRandomKey();

        try (Connection connection = DatabaseConnection.getConnection()) {

            String query = "SELECT generate_license_key_new(?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, licenseKey);
                stmt.setInt(2, duration);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String generatedKey = rs.getString(1);
                    licenseKeyField.setText(generatedKey);
                    JOptionPane.showMessageDialog(this, "License Key Generated: " + generatedKey, "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            // If auto-commit is disabled, uncomment the next line:
            // connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // SQL function to generate the license key (Random 16-character string)
    private String generateRandomKey() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder key = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            key.append(characters.charAt(randomIndex));
        }
        return key.toString();
    }

    private String getExpirationDate(int duration) {

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.MONTH, duration);
        java.util.Date expiration = calendar.getTime();
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(expiration);
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

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(new Color(64, 68, 75));
        comboBox.setForeground(Color.WHITE);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
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

    public static void main(String[] args) {
        new AdminForm().setVisible(true);
    }
}
