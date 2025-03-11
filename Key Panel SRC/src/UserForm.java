import javax.swing.*;
import java.awt.*;

public class UserForm extends JFrame {
    private String username;

    public UserForm(String username) {
        this.username = username;
        setTitle("User Panel - Activated Account");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(47, 49, 54));

        JLabel titleLabel = new JLabel("Welcome, " + username + "!", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel infoLabel = new JLabel("Your account is activated!", JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        infoLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.setBackground(new Color(53, 59, 63));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> logout());

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(47, 49, 54));
        centerPanel.add(infoLabel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(47, 49, 54));
        bottomPanel.add(logoutButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void logout() {
        new LoginForm().setVisible(true);
        dispose();
    }
}
