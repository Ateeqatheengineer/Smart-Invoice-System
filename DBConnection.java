import java.sql.*;

public class DBConnection {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/billing",
                "your_database_username",
                "your_database_password"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
