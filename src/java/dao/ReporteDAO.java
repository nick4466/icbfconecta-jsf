package dao;

import control.ConDB;
import modelo.ReporteNinoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    private static final String SELECT_BASE =
            "SELECT " +
            "    n.id_nino, " +
            "    n.nombres AS nino_nombres, " +
            "    n.apellidos AS nino_apellidos, " +
            "    n.fecha_nacimiento, " +
            "    n.documento AS nino_documento, " +
            "    n.genero, " +
            "    n.nacionalidad, " +
            "    n.fecha_ingreso, " +
            "    n.foto, " +
            "    n.carnet_vacunacion, " +
            "    n.certificado_eps, " +
            "    p.id_padre, " +
            "    u.id_usuario AS padre_usuario_id, " +
            "    u.nombres AS padre_nombres, " +
            "    u.apellidos AS padre_apellidos, " +
            "    u.documento AS padre_documento, " +
            "    u.correo AS padre_correo, " +
            "    u.telefono AS padre_telefono, " +
            "    u.direccion AS padre_direccion, " +
            "    p.ocupacion, " +
            "    p.estrato, " +
            "    p.telefono_contacto_emergencia, " +
            "    p.nombre_contacto_emergencia, " +
            "    p.situacion_economica_hogar, " +
            "    u.password_hash AS padre_password_hash, " +
            "    p.documento_identidad_img AS padre_documento_img, " +
            "    h.id_hogar, " +
            "    h.nombre_hogar, " +
            "    h.direccion AS hogar_direccion, " +
            "    h.localidad " +
            "FROM ninos n " +
            "JOIN padres p ON n.padre_id = p.id_padre " +
            "JOIN usuarios u ON p.usuario_id = u.id_usuario " +
            "JOIN hogares_comunitarios h ON n.hogar_id = h.id_hogar ";

    // Buscar un solo reporte por id de niño
    public ReporteNinoDTO findById(int idNino) {
        String sql = SELECT_BASE + "WHERE n.id_nino = ?";
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

    // Listar todos los reportes
    public List<ReporteNinoDTO> findAll() {
        List<ReporteNinoDTO> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY n.nombres, n.apellidos";
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

    // Listar reportes por madre comunitaria (según el hogar asignado)
    public List<ReporteNinoDTO> findByMadre(int idMadre) {
        List<ReporteNinoDTO> lista = new ArrayList<>();
        String sql = SELECT_BASE +
                "WHERE h.madre_id = ? " +
                "ORDER BY n.nombres, n.apellidos";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMadre);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearReporte(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Buscar un reporte validando que pertenezca a la madre indicada
    public ReporteNinoDTO findByIdAndMadre(int idNino, int idMadre) {
        String sql = SELECT_BASE +
                "WHERE n.id_nino = ? AND h.madre_id = ?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idNino);
            ps.setInt(2, idMadre);
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
