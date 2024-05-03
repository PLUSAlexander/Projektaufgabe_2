import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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

    public static void generate(int l, double sparsity) throws SQLException {
        int m = l + 1;
        int n = l + 1;

        int[][] matA = new int[m][l]; // Matrix A
        int[][] matB = new int[l][n]; // Matrix B

        for (int i = 0; i < m; i++) { // Mat. A
            for (int j = 0; j < l; j++) {
                if (RANDOM.nextDouble(1.0) < sparsity) {
                    matA[i][j] = 0;
                }
                else {
                    matA[i][j] = RANDOM.nextInt(10);
                }
            }
        }

        for (int i = 0; i < l; i++) { // Mat. B
            for (int j = 0; j < n; j++) {
                if (RANDOM.nextDouble(1.0) < sparsity) {
                    matB[i][j] = 0;
                }
                else {
                    matB[i][j] = RANDOM.nextInt(10);
                }
            }
        }

        Statement staA = con.createStatement();
        String dropA = "drop table if exists A;";
        staA.execute(dropA);

        Statement staB = con.createStatement();
        String dropB = "drop table if exists B;";
        staB.execute(dropB);

        Statement staCreA = con.createStatement();
        String createA = "create table A (i integer, j integer, val integer);";  // TODO: change val integer to double
        staCreA.execute(createA);

        Statement staCreB = con.createStatement();
        String createB = "create table B (i integer, j integer, val integer);"; // TODO: change val integer to double
        staCreB.execute(createB);


        Statement stInsertA = con.createStatement();
        StringBuilder insertA = new StringBuilder("insert into A (i, j, val) values ");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < l; j++) {
                if (matA[i][j] != 0) {
                    insertA.append("(" + (i + 1) + ", " + (j + 1) + ", " + matA[i][j] + ")");
                    if (i != m - 1 || j != l - 2) {
                        insertA.append(", ");
                    }
                }
            }
        }
        insertA.append(";");
        stInsertA.execute(insertA.toString());





    }
}