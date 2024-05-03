import javax.swing.plaf.nimbus.State;
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

    private static int[][] matA = null; // Matrix A
    private static int[][] matB = null; // Matrix B
    private static int[][] matC = null; // Matrix C

    public static void main(String[] args) throws SQLException {
        con = DriverManager.getConnection(url, user, pwd);
        generate(5, 0.1);
        matMult();
        matMultDBMS();
        con.close();
    }

    public static void generate(int l, double sparsity) throws SQLException {
        int m = l + 1;
        int n = l + 1;

        matA = new int[m][l]; // Matrix A
        matB = new int[l][n]; // Matrix B

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
        String createA = "create table A (i integer, j integer, val integer, primary key (i, j));";  // TODO: change val integer to double
        staCreA.execute(createA);

        Statement staCreB = con.createStatement();
        String createB = "create table B (i integer, j integer, val integer, primary key (i, j));"; // TODO: change val integer to double
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



        Statement stInsertB = con.createStatement();
        StringBuilder insertB = new StringBuilder("insert into B (i, j, val) values ");
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < n; j++) {
                if (matB[i][j] != 0) {
                    insertB.append("(" + (i + 1) + ", " + (j + 1) + ", " + matB[i][j] + ")");
                    if (i != l - 1 || j != n - 1) {
                        insertB.append(", ");
                    }
                }
            }
        }
        insertB.append(";");
        stInsertB.execute(insertB.toString());





    }

    public static void matMult() {
        int m = matA.length;    // Anzahl der Zeilen in Matrix A
        int n = matB[0].length; // Anzahl der Spalten in Matrix B
        int l = matA[0].length; // Anzahl der Spalten in Matrix A / Zeilen in Matrix B

        // Matrix C erstellen mit der Größe m x n
        matC = new int[m][n];

        // Durchführen der Matrixmultiplikation
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matC[i][j] = 0; // Initialisiere das Element (i, j) der Matrix C
                for (int k = 0; k < l; k++) {
                    matC[i][j] += matA[i][k] * matB[k][j];
                }
            }
        }

        // Ausgabe der Ergebnismatrix C
        System.out.println("Matrix C (Result of A * B):");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(matC[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void matMultDBMS() throws SQLException {
        Statement stCreC = con.createStatement();
        String createC = "CREATE TEMPORARY TABLE C (i integer, j integer, sum integer);";  // TODO: change to double
        stCreC.execute(createC);

        Statement stInsertRes = con.createStatement();
        String insertRes = "INSERT INTO C SELECT A.i, B.j, SUM(A.val*B.val) FROM A, B WHERE A.j = B.i GROUP BY A.i, B.j;";
        stInsertRes.execute(insertRes);
    }

}