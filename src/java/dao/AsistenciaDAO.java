package dao;

import control.ConDB;
import modelo.Asistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsistenciaDAO {

    private static final Logger LOGGER = Logger.getLogger(AsistenciaDAO.class.getName());

// --------------------------Insertar registro de asistencia------------------------------------------
public boolean insertar(Asistencia a) {
    String sql = "INSERT INTO asistencia (id_nino, fecha, estado) VALUES (?, ?, ?)";

    try (Connection con = ConDB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, a.getIdNino());   // Relación con el niño
        ps.setDate(2, a.getFecha());   // Fecha de la asistencia
        ps.setString(3, a.getEstado()); // Estado: presente, ausente, etc.

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error al insertar asistencia", e);
        return false;
    }
}



//---------------- Lista todas las asistencias junto con el nombre y apellido del niño.----------
public List<Asistencia> listarConNombres() {
    return listarInterno(null);
}

public List<Asistencia> listarPorMadre(int idMadre) {
    return listarInterno(idMadre);
}

private List<Asistencia> listarInterno(Integer idMadre) {
    List<Asistencia> lista = new ArrayList<>();
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT a.id_asistencia, a.id_nino, a.fecha, a.estado, ")
       .append("n.nombres AS nino_nombres, n.apellidos AS nino_apellidos, ")
       .append("u.nombres AS madre_nombres, u.apellidos AS madre_apellidos, ")
       .append("h.nombre_hogar ")
       .append("FROM asistencia a ")
       .append("JOIN ninos n ON a.id_nino = n.id_nino ")
       .append("JOIN hogares_comunitarios h ON n.hogar_id = h.id_hogar ")
       .append("LEFT JOIN usuarios u ON h.madre_id = u.id_usuario ");

    if (idMadre != null) {
        sql.append("WHERE h.madre_id = ? ");
    }

    sql.append("ORDER BY a.fecha DESC, n.nombres, n.apellidos");

    try (Connection con = ConDB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql.toString())) {

        if (idMadre != null) {
            ps.setInt(1, idMadre);
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Asistencia a = new Asistencia();
                a.setIdAsistencia(rs.getInt("id_asistencia"));
                a.setIdNino(rs.getInt("id_nino"));
                a.setNombres(rs.getString("nino_nombres"));
                a.setApellidos(rs.getString("nino_apellidos"));
                a.setFecha(rs.getDate("fecha"));
                a.setEstado(rs.getString("estado"));
                a.setMadreNombres(rs.getString("madre_nombres"));
                a.setMadreApellidos(rs.getString("madre_apellidos"));
                a.setNombreHogar(rs.getString("nombre_hogar"));
                lista.add(a);
            }
        }

    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error al listar asistencias", e);
    }
    return lista;
}




    //----------------------- Buscar asistencia por ID ---------------------
    public Asistencia buscarPorId(int idAsistencia) {
        String sql = "SELECT * FROM asistencia WHERE id_asistencia = ?";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAsistencia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAsistencia(rs);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar asistencia por ID", e);
        }
        return null;
    }

    // Eliminar asistencia
    public boolean eliminar(int idAsistencia) {
        String sql = "DELETE FROM asistencia WHERE id_asistencia = ?";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAsistencia);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar asistencia", e);
            return false;
        }
    }

    // -----------------------------Mapear ResultSet -> Asistencia --------------------------------------------------------
    private Asistencia mapearAsistencia(ResultSet rs) throws SQLException {
        Asistencia a = new Asistencia();
        a.setIdAsistencia(rs.getInt("id_asistencia"));
        a.setIdNino(rs.getInt("id_nino"));
        a.setFecha(rs.getDate("fecha"));
        a.setEstado(rs.getString("estado"));
        return a;
    }
    
    // ==================
// Actualizar asistencia
// ==================
public boolean actualizar(Asistencia a) {
    String sql = "UPDATE asistencia SET id_nino=?, fecha=?, estado=? WHERE id_asistencia=?";
    try (Connection con = ConDB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, a.getIdNino());
        ps.setDate(2, a.getFecha());
        ps.setString(3, a.getEstado());
        ps.setInt(4, a.getIdAsistencia());

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error al actualizar asistencia", e);
        return false;
    }
}

}
