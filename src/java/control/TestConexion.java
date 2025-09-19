package control;

import java.sql.Connection;

public class TestConexion {
    public static void main(String[] args) {
        Connection conn = ConDB.getConnection();
        if (conn != null) {
            System.out.println("✅ Conexión exitosa");
        } else {
            System.out.println("❌ No se pudo conectar");
        }
    }
}
