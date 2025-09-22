package dao;

import control.ConDB;
import modelo.ReporteNinoDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    // Buscar un solo reporte por id de niño
    public ReporteNinoDTO findById(int idNino) {
        String sql = "SELECT vw.*, u.password_hash AS padre_password_hash, " +
                     "p.documento_identidad_img AS padre_documento_img " +
                     "FROM vw_reporte_nino vw " +
                     "JOIN usuarios u ON vw.padre_usuario_id = u.id_usuario " +
                     "JOIN padres p ON p.id_padre = vw.id_padre " +
                     "WHERE vw.id_nino = ?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idNino);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearReporte(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Listar todos los reportes (o por hogar si quieres)
    public List<ReporteNinoDTO> findAll() {
        List<ReporteNinoDTO> lista = new ArrayList<>();
        String sql = "SELECT vw.*, u.password_hash AS padre_password_hash, " +
                     "p.documento_identidad_img AS padre_documento_img " +
                     "FROM vw_reporte_nino vw " +
                     "JOIN usuarios u ON vw.padre_usuario_id = u.id_usuario " +
                     "JOIN padres p ON p.id_padre = vw.id_padre";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearReporte(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Mapper ResultSet -> DTO
    private ReporteNinoDTO mapearReporte(ResultSet rs) throws SQLException {
        ReporteNinoDTO dto = new ReporteNinoDTO();

        // Niño
        dto.setIdNino(rs.getInt("id_nino"));
        dto.setNinoNombres(rs.getString("nino_nombres"));
        dto.setNinoApellidos(rs.getString("nino_apellidos"));
        dto.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        dto.setNinoDocumento(getLong(rs, "nino_documento"));
        dto.setGenero(rs.getString("genero"));
        dto.setNacionalidad(rs.getString("nacionalidad"));
        dto.setFechaIngreso(rs.getDate("fecha_ingreso"));
        dto.setFoto(rs.getString("foto"));
        dto.setCarnetVacunacion(rs.getString("carnet_vacunacion"));
        dto.setCertificadoEps(rs.getString("certificado_eps"));

        // Padre
        dto.setIdPadre(rs.getInt("id_padre"));
        dto.setPadreUsuarioId(rs.getInt("padre_usuario_id"));
        dto.setPadreNombres(rs.getString("padre_nombres"));
        dto.setPadreApellidos(rs.getString("padre_apellidos"));
        dto.setPadreDocumento(rs.getString("padre_documento"));
        dto.setPadreCorreo(rs.getString("padre_correo"));
        dto.setPadreTelefono(rs.getString("padre_telefono"));
        dto.setPadreDireccion(rs.getString("padre_direccion"));
        dto.setOcupacion(rs.getString("ocupacion"));
        dto.setEstrato(getInteger(rs, "estrato"));
        dto.setTelEmerg(rs.getString("telefono_contacto_emergencia"));
        dto.setNomEmerg(rs.getString("nombre_contacto_emergencia"));
        dto.setSituacionEcon(rs.getString("situacion_economica_hogar"));
        dto.setPadrePasswordHash(rs.getString("padre_password_hash"));
        dto.setDocumentoPadreImg(rs.getString("padre_documento_img"));

        // Hogar
        dto.setIdHogar(rs.getInt("id_hogar"));
        dto.setNombreHogar(rs.getString("nombre_hogar"));
        dto.setHogarDireccion(rs.getString("hogar_direccion"));
        dto.setLocalidad(rs.getString("localidad"));

        return dto;
    }

    private Long getLong(ResultSet rs, String column) throws SQLException {
        Object value = rs.getObject(column);
        return (value instanceof Number) ? ((Number) value).longValue() : null;
    }

    private Integer getInteger(ResultSet rs, String column) throws SQLException {
        Object value = rs.getObject(column);
        return (value instanceof Number) ? ((Number) value).intValue() : null;
    }
}
