import java.sql.*;
import java.util.*;

public class Sofia {
    private static final Random RANDOM = new Random(71);
    private static Connection con;
    private static String url = "jdbc:postgresql://localhost/DJRProjektaufgabe2";
    private static String user = "postgres";
    private static String pwd = "dreizehn13";
    private static double[][] matA = null; // Matrix A
    private static double[][] matB = null; // Matrix B
    private static double[][] matC = null; // Matrix C



    public static void main(String[] args) throws SQLException {
        con = DriverManager.getConnection(url, user, pwd);

        generate(6, 0.1);
        import_approach1();
        matMulti();
        matMultiDBMS();
        import_approach2();

        con.close();
    }



    //Phase 1

    public static void generate(int l, double sparsity) throws SQLException {
        int m = l + 1;
        int n = l + 1;

        matA = new double[m][l]; //A
        matB = new double[l][n]; //B


        //fill Matrix A
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < l; j++) {
                if (RANDOM.nextDouble(1.0) < sparsity) {
                    matA[i][j] = 0;
                } else {
                    matA[i][j] = RANDOM.nextInt(14) + 1;
                }
            }
        }


        //fill Matrix B
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < n; j++) {
                if (RANDOM.nextDouble(1.0) < sparsity) {
                    matB[i][j] = 0;
                } else {
                    matB[i][j] = RANDOM.nextInt(14) + 1;
                }
            }
        }

        System.out.println("Matrix A and B successfully created.");


        /* System.out.println("\nMatrix A: ");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < l; j++) {
                System.out.print(matA[i][j] + "     ");
            }
            System.out.println();
        } */

        /* System.out.println("\nMatrix B: ");
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(matB[i][j] + "     ");
            }
            System.out.println();
        } */


        //System.out.println(Arrays.deepToString(matA));
        //System.out.println(Arrays.deepToString(matB));
    }


    public static void import_approach1() throws SQLException {
        int m = matA.length;
        int n = matB[0].length;
        int l = matA[0].length;

        //create tables
        Statement staDropA = con.createStatement();
        String dropA = "drop table if exists A;";
        staDropA.execute(dropA);

        Statement staDropB = con.createStatement();
        String dropB = "drop table if exists B;";
        staDropB.execute(dropB);

        Statement staCreA = con.createStatement();
        String createA = "create table A (i integer, j integer, val double precision, primary key (i, j));";
        staCreA.execute(createA);

        Statement staCreB = con.createStatement();
        String createB = "create table B (i integer, j integer, val double precision, primary key (i, j));";
        staCreB.execute(createB);


        //insert values into tables
        Statement stInsertA = con.createStatement();
        StringBuilder insertA = new StringBuilder("insert into A (i, j, val) values ");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < l; j++) {
                if (matA[i][j] != 0) {
                    insertA.append("(").append(i + 1).append(", ").append(j + 1).append(", ").append(matA[i][j]).append(")");
                    if (i != m - 1 || j != l - 1) {
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
                    insertB.append("(").append(i + 1).append(", ").append(j + 1).append(", ").append(matB[i][j]).append(")");
                    if (i != l - 1 || j != n - 1) {
                        insertB.append(", ");
                    }
                }
            }
        }
        insertB.append(";");
        stInsertB.execute(insertB.toString());

        System.out.println("Matrix A and B successfully imported.");
    }


    //Ansatz 0
    public static void matMulti() {
        int m = matA.length;    //# rows in Matrix A
        int n = matB[0].length; //# columns in Matrix B
        int l = matA[0].length; //# columns in Matrix A = # rows in Matrix B

        //create Matrix with size m x n
        matC = new double[m][n];

        //perform Matrix-Multiplication
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matC[i][j] = 0; //initialize element (i, j) of Matrix C
                for (int k = 0; k < l; k++) {
                    matC[i][j] += matA[i][k] * matB[k][j];
                }
            }
        }

        //put result Matrix out
        System.out.println("\nMatrix C (Result of A * B): ");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(matC[i][j] + "     ");
            }
            System.out.println();
        }
        System.out.println("\n");
    }


    //Ansatz 1
    public static void matMultiDBMS() throws SQLException {
        Statement stCreC = con.createStatement();
        String createC = "CREATE TEMPORARY TABLE C (i integer, j integer, sum double precision);";
        stCreC.execute(createC);

        Statement stInsertRes = con.createStatement();
        String insertRes = "INSERT INTO C SELECT A.i, B.j, SUM(A.val*B.val) FROM A, B WHERE A.j = B.i GROUP BY A.i, B.j ORDER BY i ASC, j ASC;";
        stInsertRes.execute(insertRes);
    }



    //Phase 2
    public static void import_approach2() throws SQLException {
        int m = matA.length;
        int n = matB[0].length;
        int l = matA[0].length;

       //create tables
        Statement staDropA = con.createStatement();
        String dropA = "drop table if exists A_Arr;";
        staDropA.execute(dropA);

        Statement staDropB = con.createStatement();
        String dropB = "drop table if exists B_Arr;";
        staDropB.execute(dropB);

        Statement staCreA = con.createStatement();
        String createA = "create table A_arr (i integer, row double precision[]);";
        staCreA.execute(createA);

        Statement staCreB = con.createStatement();
        String createB = "create table B_arr (j integer, col double precision[]);";
        staCreB.execute(createB);


        //insert values into tables
        Statement stInsertA = con.createStatement();
        StringBuilder insertA = new StringBuilder("insert into A_arr (i, row) values ");

        for (int i = 0; i < m; i++) {
            insertA.append("(").append(i + 1).append(", '{");
            for (int j = 0; j < l; j++) {
                insertA.append(matA[i][j]);
                if (j != l - 1) {
                    insertA.append(", ");
                }
            }
            insertA.append("}')");
            if (i != m - 1){
                insertA.append(", ");
            }
        }
        insertA.append(";");
        //System.out.println(insertA);
        stInsertA.execute(insertA.toString());


        Statement stInsertB = con.createStatement();
        StringBuilder insertB = new StringBuilder("insert into B_arr (j, col) values ");

        for (int i = 0; i < n; i++) {
        insertB.append("(").append(i + 1).append(", '{");
            for (int j = 0; j < l; j++) {
                insertB.append(matB[j][i]);
                if (j != l - 1) {
                    insertB.append(", ");
                }
            }
            insertB.append("}')");
            if (i != n - 1){
                insertB.append(", ");
            }
        }
        insertB.append(";");
        //System.out.println(insertB);
        stInsertB.execute(insertB.toString());

        System.out.println("Matrix A and B successfully imported with arrays.");

    }
}
