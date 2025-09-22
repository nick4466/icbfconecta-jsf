package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Desarrollo;

public class DesarrolloDAO {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/conecta_icbf_v2";
        String user = "root";
        String pass = "";
        System.out.println("[DEBUG] Intentando conectar a la BD...");
        Connection con = DriverManager.getConnection(url, user, pass);
        System.out.println("[DEBUG] Conexión exitosa a la BD");
        return con;
    }

    public List<Desarrollo> listar() {
        List<Desarrollo> lista = new ArrayList<>();
        String sql = "SELECT d.id_desarrollo, d.id_nino, n.nombres AS nombreNino, d.fecha_fin_mes, " +
                     "d.dimension_cognitiva, d.dimension_comunicativa, d.dimension_socio_afectiva, d.dimension_corporal, d.fecha_registro " +
                     "FROM desarrollo_nino d INNER JOIN ninos n ON d.id_nino = n.id_nino ORDER BY d.fecha_fin_mes DESC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("[DEBUG] Consulta listar ejecutada");
            while (rs.next()) {
                Desarrollo d = new Desarrollo();
                d.setIdDesarrollo(rs.getInt("id_desarrollo"));
                d.setIdNino(rs.getInt("id_nino"));
                d.setNombreNino(rs.getString("nombreNino"));
                d.setFechaFinMes(rs.getDate("fecha_fin_mes"));
                d.setDimensionCognitiva(rs.getString("dimension_cognitiva"));
                d.setDimensionComunicativa(rs.getString("dimension_comunicativa"));
                d.setDimensionSocioAfectiva(rs.getString("dimension_socio_afectiva"));
                d.setDimensionCorporal(rs.getString("dimension_corporal"));
                d.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                lista.add(d);
            }
            System.out.println("[DEBUG] Registros encontrados: " + lista.size());
        } catch (SQLException e) {
            System.out.println("[ERROR] SQLException en listar: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public void insertar(Desarrollo d) throws SQLException {
        String sql = "INSERT INTO desarrollo_nino (id_nino, fecha_fin_mes, dimension_cognitiva, dimension_comunicativa, dimension_socio_afectiva, dimension_corporal) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            System.out.println("[DEBUG] Insertando desarrollo: idNino=" + d.getIdNino());
            ps.setInt(1, d.getIdNino());
            // Conversión segura a java.sql.Date
            if (d.getFechaFinMes() != null) {
                ps.setDate(2, new java.sql.Date(d.getFechaFinMes().getTime()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setString(3, d.getDimensionCognitiva());
            ps.setString(4, d.getDimensionComunicativa());
            ps.setString(5, d.getDimensionSocioAfectiva());
            ps.setString(6, d.getDimensionCorporal());
            int filas = ps.executeUpdate();
            System.out.println("[DEBUG] Filas insertadas: " + filas);
        } catch (SQLException e) {
            System.out.println("[ERROR] SQLException en insertar: " + e.getMessage());
            throw e;
        }
    }

    public void actualizar(Desarrollo d) throws SQLException {
        String sql = "UPDATE desarrollo_nino SET id_nino=?, fecha_fin_mes=?, dimension_cognitiva=?, dimension_comunicativa=?, dimension_socio_afectiva=?, dimension_corporal=? WHERE id_desarrollo=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getIdNino());
            // Conversión segura a java.sql.Date
            if (d.getFechaFinMes() != null) {
                ps.setDate(2, new java.sql.Date(d.getFechaFinMes().getTime()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setString(3, d.getDimensionCognitiva());
            ps.setString(4, d.getDimensionComunicativa());
            ps.setString(5, d.getDimensionSocioAfectiva());
            ps.setString(6, d.getDimensionCorporal());
            ps.setInt(7, d.getIdDesarrollo());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM desarrollo_nino WHERE id_desarrollo=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
