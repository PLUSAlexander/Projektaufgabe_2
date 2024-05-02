import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

public class Sofia {
    private static final Random RANDOM = new Random(42);
    private static String url = "jdbc:postgresql://localhost/postgres";
    private static String user = "postgres";
    private static String pwd = "1234";
    private static Connection con;

    public static void main(String[] args) throws SQLException {
        con = DriverManager.getConnection(url, user, pwd);

        con.close();
    }
}
