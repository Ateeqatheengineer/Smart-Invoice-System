import java.sql.*;

public class DBConnection {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/billing",
                "root",
                "ateeqa5g"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
