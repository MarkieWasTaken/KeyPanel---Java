import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ListKeysForm extends JFrame {
    private JTable keysTable;
    private JButton backButton;

    public ListKeysForm() {
        setTitle("Keys List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Table to display key data
        String[][] keysData = getKeysFromDatabase();
        String[] columnNames = {"Key ID", "Key Value", "Is Used", "User", "Actions"};

        // Create a custom table model with the "Actions" column as buttons
        keysTable = new JTable(new CustomTableModel(keysData, columnNames)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Allow editing only in the "Actions" column (buttons)
            }
        };

        // Add button to each row in the "Actions" column
        keysTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        keysTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(keysTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add back button to return to the previous screen
        backButton = new JButton("Back to Admin Form");
        backButton.addActionListener(e -> {
            new AdminForm().setVisible(true);
            dispose();
        });
        panel.add(backButton, BorderLayout.SOUTH);

        add(panel);
    }

    private String[][] getKeysFromDatabase() {
        String[][] data = new String[0][];
        String query = "SELECT l.id AS license_id, l.license_key, l.active AS is_used, u.username " +
                "FROM license l " +
                "LEFT JOIN license_user lu ON l.id = lu.license_id " +
                "LEFT JOIN users u ON lu.user_id = u.id;";

        try (Connection connection = DatabaseConnection.getConnection()) {
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(query);

            rs.last(); // Move to the last row to get the row count
            int rowCount = rs.getRow();
            rs.beforeFirst(); // Move back to the first row

            data = new String[rowCount][5]; // 5 columns: Key ID, Key Value, Is Used, User, Actions
            int rowIndex = 0;

            while (rs.next()) {
                data[rowIndex][0] = String.valueOf(rs.getInt("license_id"));
                data[rowIndex][1] = rs.getString("license_key");
                data[rowIndex][2] = rs.getBoolean("is_used") ? "Yes" : "No";
                data[rowIndex][3] = rs.getString("username"); // User associated with the key
                data[rowIndex][4] = "Delete"; // Placeholder text for the button
                rowIndex++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    // Method to delete a key
    private void deleteKey(int keyId) {
        String query = "DELETE FROM license WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, keyId);
            stmt.execute();
            JOptionPane.showMessageDialog(this, "License key deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting license key: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            if (column == 4) {
                return JButton.class; // "Actions" column should contain buttons
            }
            return String.class;
        }
    }

    // Button renderer for rendering buttons in the "Actions" column
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setText("Delete");
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
        private int keyId;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Delete");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = keysTable.getSelectedRow();
                    if (row != -1) {
                        keyId = Integer.parseInt((String) keysTable.getValueAt(row, 0)); // Get keyId from the selected row
                        deleteKey(keyId); // Delete the key
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
        new ListKeysForm().setVisible(true);
    }
}
