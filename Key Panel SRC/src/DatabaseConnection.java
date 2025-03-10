import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final Dotenv dotenv = Dotenv.configure().load(); // Auto-detect .env

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver"); // Use PostgreSQL driver
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found.");
            e.printStackTrace();
            throw new SQLException("PostgreSQL JDBC Driver not found.");
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
