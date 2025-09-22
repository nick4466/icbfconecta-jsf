package dao;

import control.ConDB;
import modelo.Nino;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NinoDAO {

    private static final Logger LOGGER = Logger.getLogger(NinoDAO.class.getName());

    // Insertar niño y devolver id
    public int insert(Nino n) throws SQLException {
        String sql = "INSERT INTO ninos (nombres, apellidos, fecha_nacimiento, documento, genero, nacionalidad, fecha_ingreso, hogar_id, padre_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, n.getNombres());
            ps.setString(2, n.getApellidos());

            if (n.getFechaNacimiento() != null) {
                ps.setDate(3, new java.sql.Date(n.getFechaNacimiento().getTime()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            if (n.getDocumento() != null) {
                ps.setLong(4, n.getDocumento());
            } else {
                ps.setNull(4, Types.BIGINT);
            }

            ps.setString(5, n.getGenero());
            ps.setString(6, n.getNacionalidad());

            if (n.getFechaIngreso() != null) {
                ps.setDate(7, new java.sql.Date(n.getFechaIngreso().getTime()));
            } else {
                ps.setDate(7, new java.sql.Date(System.currentTimeMillis()));
            }

            ps.setInt(8, n.getHogarId());
            ps.setInt(9, n.getPadreId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo insertar niño");
    }

    // Actualizar foto
    public void updateFoto(int idNino, String rutaFoto) throws SQLException {
        String sql = "UPDATE ninos SET foto = ? WHERE id_nino = ?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rutaFoto); // ruta relativa
            ps.setInt(2, idNino);
            ps.executeUpdate();
        }
    }

    // Listar niños por hogar
    public List<Nino> listarNinosPorHogar(int idHogar) {
        List<Nino> lista = new ArrayList<>();
        String sql = "SELECT * FROM ninos WHERE hogar_id = ?";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idHogar);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearNino(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar niños por hogar", e);
        }
        return lista;
    }

    // Buscar niño por ID
    public Nino buscarNinoPorId(int idNino) {
        String sql = "SELECT * FROM ninos WHERE id_nino = ?";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idNino);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearNino(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar niño por ID", e);
        }
        return null;
    }

    // Actualizar niño completo
    public boolean actualizarNino(Nino n) {
        String sql = "UPDATE ninos SET nombres=?, apellidos=?, fecha_nacimiento=?, genero=?, nacionalidad=?, foto=? WHERE id_nino=?";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, n.getNombres());
            ps.setString(2, n.getApellidos());

            if (n.getFechaNacimiento() != null) {
                ps.setDate(3, new java.sql.Date(n.getFechaNacimiento().getTime()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            ps.setString(4, n.getGenero());
            ps.setString(5, n.getNacionalidad());
            ps.setString(6, n.getFoto()); // si no se sube nueva foto, queda la existente
            ps.setInt(7, n.getIdNino());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar niño", e);
            return false;
        }
    }

    // Eliminar niño
    public boolean eliminarNino(int idNino) {
        String sql = "DELETE FROM ninos WHERE id_nino = ?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idNino);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar niño", e);
            return false;
        }
    }

    // Mapear ResultSet -> Nino
    private Nino mapearNino(ResultSet rs) throws SQLException {
        Nino n = new Nino();
        n.setIdNino(rs.getInt("id_nino"));
        n.setNombres(rs.getString("nombres"));
        n.setApellidos(rs.getString("apellidos"));

        java.sql.Date sqlFechaNac = rs.getDate("fecha_nacimiento");
        if (sqlFechaNac != null) {
            n.setFechaNacimiento(new java.util.Date(sqlFechaNac.getTime()));
        }

        long doc = rs.getLong("documento");
        if (!rs.wasNull()) {
            n.setDocumento(doc);
        }

        n.setGenero(rs.getString("genero"));
        n.setNacionalidad(rs.getString("nacionalidad"));

        java.sql.Date sqlFechaIng = rs.getDate("fecha_ingreso");
        if (sqlFechaIng != null) {
            n.setFechaIngreso(new java.util.Date(sqlFechaIng.getTime()));
        }

        n.setHogarId(rs.getInt("hogar_id"));
        n.setPadreId(rs.getInt("padre_id"));
        n.setFoto(rs.getString("foto"));
        n.setCarnetVacunacion(rs.getString("carnet_vacunacion"));
        n.setCertificadoEps(rs.getString("certificado_eps"));

        return n;
    }
    
    public List<Nino> listar() {
    List<Nino> lista = new ArrayList<>();
    String sql = "SELECT id_nino, nombres, apellidos FROM ninos";

    try (Connection con = ConDB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Nino n = new Nino();
            n.setIdNino(rs.getInt("id_nino"));
            n.setNombres(rs.getString("nombres"));
            n.setApellidos(rs.getString("apellidos"));
            lista.add(n);
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error al listar niños", e);
    }
    return lista;
}
}
