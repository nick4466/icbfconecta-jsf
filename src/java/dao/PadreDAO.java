package dao;

import control.ConDB;
import modelo.Padre;

import java.sql.*;

public class PadreDAO {

    // =========================
    // Insertar padre (extensión del usuario)
    // =========================
    public int insert(Padre padre) throws SQLException {
        String sql = "INSERT INTO padres (ocupacion, estrato, telefono_contacto_emergencia, " +
                     "nombre_contacto_emergencia, situacion_economica_hogar, documento_identidad_img, usuario_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, padre.getOcupacion());

            if (padre.getEstrato() != null) {
                ps.setInt(2, padre.getEstrato());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, padre.getTelefonoContactoEmergencia());
            ps.setString(4, padre.getNombreContactoEmergencia());
            ps.setString(5, padre.getSituacionEconomicaHogar());
            ps.setString(6, padre.getDocumentoIdentidadImg()); // ruta del archivo
            ps.setInt(7, padre.getUsuarioId()); // FK a usuarios

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar padre");
    }

    // =========================
    // Actualizar documento de identidad
    // =========================
    public void updateDocumento(int idPadre, String rutaDocumento) throws SQLException {
        String sql = "UPDATE padres SET documento_identidad_img = ? WHERE id_padre = ?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rutaDocumento);
            ps.setInt(2, idPadre);
            ps.executeUpdate();
        }
    }

    // =========================
    // Buscar padre por usuario_id
    // =========================
    public Padre findByUsuarioId(int usuarioId) {
        String sql = "SELECT * FROM padres WHERE usuario_id = ?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Padre p = new Padre();
                    p.setIdPadre(rs.getInt("id_padre"));
                    p.setUsuarioId(rs.getInt("usuario_id"));
                    p.setOcupacion(rs.getString("ocupacion"));
                    p.setEstrato(rs.getInt("estrato"));
                    p.setTelefonoContactoEmergencia(rs.getString("telefono_contacto_emergencia"));
                    p.setNombreContactoEmergencia(rs.getString("nombre_contacto_emergencia"));
                    p.setSituacionEconomicaHogar(rs.getString("situacion_economica_hogar"));
                    p.setDocumentoIdentidadImg(rs.getString("documento_identidad_img"));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
