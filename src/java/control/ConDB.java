package control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

   public class ConDB {
    private static final String URL = "jdbc:mysql://localhost:3306/conecta_icbf_v2?user=root&useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
   }