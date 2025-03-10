import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ListUsersForm extends JFrame {
    private JTable usersTable;
    private JButton backButton;

    public ListUsersForm() {
        setTitle("Users List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Table to display user data
        String[][] usersData = getUsersFromDatabase();
        String[] columnNames = {"User ID", "Username", "Email", "Location", "Is Admin", "Actions"};

        // Create a custom table model with the "Actions" column as buttons
        usersTable = new JTable(new CustomTableModel(usersData, columnNames)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Allow editing only in the "Actions" column (buttons)
            }
        };

        // Add button to each row in the "Actions" column
        usersTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        usersTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add back button to return to the previous screen
        backButton = new JButton("Back to Key Generation");
        backButton.addActionListener(e -> {
            new AdminForm().setVisible(true);
            dispose();
        });
        panel.add(backButton, BorderLayout.SOUTH);

        add(panel);
    }

    private String[][] getUsersFromDatabase() {
        String[][] data = new String[0][];
        String query = "SELECT * FROM get_users_list();"; // Call the SQL function

        try (Connection connection = DatabaseConnection.getConnection()) {
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(query);

            rs.last(); // Move to the last row to get the row count
            int rowCount = rs.getRow();
            rs.beforeFirst(); // Move back to the first row

            data = new String[rowCount][6]; // Add an extra column for actions
            int rowIndex = 0;

            while (rs.next()) {
                data[rowIndex][0] = String.valueOf(rs.getInt("user_id"));
                data[rowIndex][1] = rs.getString("username");
                data[rowIndex][2] = rs.getString("email");
                data[rowIndex][3] = rs.getString("location_name");
                data[rowIndex][4] = rs.getBoolean("is_admin") ? "Yes" : "No";
                data[rowIndex][5] = "Toggle Admin"; // Placeholder text for the button
                rowIndex++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    // Method to toggle admin status of a user
    private void toggleAdminStatus(int userId) {
        String query = "SELECT toggle_admin_status(?);"; //DET ZAKA RACAS RESOLT CE SI VOID

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.execute();
            JOptionPane.showMessageDialog(this, "Admin status updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating admin status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Custom table model for handling buttons in the "Actions" column
    private class CustomTableModel extends javax.swing.table.AbstractTableModel {
        private String[][] data;
        private String[] columnNames;

        public CustomTableModel(String[][] data, String[] columnNames) {
            this.data = data;
            this.columnNames = columnNames;
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            return data[row][column];
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int column) {
            if (column == 5) {
                return JButton.class; // "Actions" column should contain buttons
            }
            return String.class;
        }
    }

    // Button renderer for rendering buttons in the "Actions" column
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setText("Toggle Admin");
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Button editor for handling button clicks
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int userId;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Toggle Admin");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = usersTable.getSelectedRow();
                    if (row != -1) {
                        userId = Integer.parseInt((String) usersTable.getValueAt(row, 0)); // Get userId from the selected row
                        toggleAdminStatus(userId); // Toggle admin status
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }
    }

    public static void main(String[] args) {
        new ListUsersForm().setVisible(true);
    }
}
