import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

public class Alexander {
    private static final Random RANDOM = new Random(42);
    private static String url = "jdbc:postgresql://localhost/postgres";
    private static String user = "postgres";
    private static String pwd = "1234";
    private static Connection con;

    public static void main(String[] args) throws SQLException {
        con = DriverManager.getConnection(url, user, pwd);
        generate(5, 0.1);
        con.close();
    }

    public static void generate(int l, double sparsity) {
        int m = l + 1;
        int n = l + 1;

        double[][] matA = new double[m][l]; // Matrix A
        double[][] matB = new double[l][n]; // Matrix B

        for (int i = 0; i < m; i++) { // Mat. A
            for (int j = 0; j < l; j++) {
                if (RANDOM.nextDouble(1.0) < sparsity) {
                    matA[i][j] = 0.0;
                }
                else {
                    matA[i][j] = RANDOM.nextDouble(10);
                }
            }
        }

        for (int i = 0; i < l; i++) { // Mat. B
            for (int j = 0; j < n; j++) {
                if (RANDOM.nextDouble(1.0) < sparsity) {
                    matB[i][j] = 0.0;
                }
                else {
                    matB[i][j] = RANDOM.nextDouble(10);
                }
            }
        }






    }
}