import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AdminForm extends JFrame {
    private JComboBox<String> durationComboBox;
    private JTextField licenseKeyField;
    private JButton generateKeyButton, listUsersButton, goToListKeysButton, sendToExcelButton;

    public AdminForm() {
        setTitle("Admin Panel");
        setSize(500, 550);  // Increased height to fit new button
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
        generateKeyButton.addActionListener(e -> generateLicenseKey());

        listUsersButton = createStyledButton("Go to List Users");
        listUsersButton.addActionListener(e -> {
            new ListUsersForm().setVisible(true);
            dispose();
        });

        goToListKeysButton = createStyledButton("Go to List Keys");
        goToListKeysButton.addActionListener(e -> {
            new ListKeysForm().setVisible(true);
            dispose();
        });

        sendToExcelButton = createStyledButton("Send to Excel");
        sendToExcelButton.addActionListener(e -> generateExcelWithData());

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
        formPanel.add(goToListKeysButton, gbc);
        gbc.gridy++;
        formPanel.add(sendToExcelButton, gbc);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        add(panel);
    }

    private void generateLicenseKey() {
        // Generate a random license key
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder key = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            key.append(characters.charAt(randomIndex));
        }
        String generatedKey = key.toString();
        licenseKeyField.setText(generatedKey);  // Display generated key

        // Get the selected duration
        String selectedDuration = (String) durationComboBox.getSelectedItem();
        int durationInMonths = getDurationInMonths(selectedDuration);

        // Call the SQL function to insert the generated key into the database
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT generate_license_key_new(?, ?)";  // Correct way to call the function

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                // Set input parameters for the SQL function
                stmt.setString(1, generatedKey);  // Pass the generated key
                stmt.setInt(2, durationInMonths);  // Pass the selected duration

                // Execute the query
                ResultSet rs = stmt.executeQuery();

                // Fetch the result (the generated license key)
                if (rs.next()) {
                    String returnedLicenseKey = rs.getString(1);
                    JOptionPane.showMessageDialog(this, "License key generated and stored successfully: " + returnedLicenseKey, "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error calling SQL function: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getDurationInMonths(String selectedDuration) {
        switch (selectedDuration) {
            case "1 Month":
                return 1;
            case "6 Months":
                return 6;
            case "1 Year":
                return 12;
            default:
                return 1; // Default to 1 month
        }
    }

    private void generateExcelWithData() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT u.username, u.email, l.license_key, l.duration, l.expiration_date " +
                    "FROM users u " +
                    "JOIN license_user lu ON u.id = lu.user_id " +
                    "JOIN license l ON lu.license_id = l.id";

            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                try (Workbook workbook = new XSSFWorkbook()) {
                    Sheet sheet = workbook.createSheet("Users and Keys");
                    Row headerRow = sheet.createRow(0);

                    headerRow.createCell(0).setCellValue("Username");
                    headerRow.createCell(1).setCellValue("Email");
                    headerRow.createCell(2).setCellValue("License Key");
                    headerRow.createCell(3).setCellValue("Duration");
                    headerRow.createCell(4).setCellValue("Expiration Date");

                    int rowNum = 1;
                    while (rs.next()) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(rs.getString("username"));
                        row.createCell(1).setCellValue(rs.getString("email"));
                        row.createCell(2).setCellValue(rs.getString("license_key"));
                        row.createCell(3).setCellValue(rs.getInt("duration"));
                        row.createCell(4).setCellValue(rs.getTimestamp("expiration_date"));
                    }

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save Excel File");
                    fileChooser.setSelectedFile(new File("users_and_keys.xlsx"));
                    int userSelection = fileChooser.showSaveDialog(this);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                            workbook.write(fileOut);
                        }
                        JOptionPane.showMessageDialog(this, "Excel file created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error generating Excel file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
