package dao;

import control.ConDB;
import static control.ConDB.getConnection;
import java.security.NoSuchAlgorithmException;
import modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.PadreCreado;

public class UsuarioDAO {

    // ========================
    // Validación de login
    // ========================
    public Usuario validarLogin(String documento, String password) {
        String sql = "SELECT u.id_usuario, u.documento, u.nombres, u.apellidos, " +
                     "u.correo, u.direccion, u.telefono, r.nombre_rol " +
                     "FROM usuarios u " +
                     "JOIN roles r ON u.rol_id = r.id_rol " +
                     "WHERE u.documento = ? AND u.password_hash = SHA2(?, 256)";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, documento);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario user = new Usuario();
                    user.setIdUsuario(rs.getInt("id_usuario"));
                    user.setDocumento(rs.getString("documento"));
                    user.setNombres(rs.getString("nombres"));
                    user.setApellidos(rs.getString("apellidos"));
                    user.setCorreo(rs.getString("correo"));
                    user.setDireccion(rs.getString("direccion"));
                    user.setTelefono(rs.getString("telefono"));
                    user.setRol(rs.getString("nombre_rol"));
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // si no hay coincidencia
    }

    // ========================
    // Listar madres comunitarias
    // ========================
    public List<Usuario> obtenerMadresComunitarias() {
        List<Usuario> madres = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.documento, u.nombres, u.apellidos, u.correo, u.telefono " +
                     "FROM usuarios u " +
                     "JOIN roles r ON u.rol_id = r.id_rol " +
                     "WHERE r.nombre_rol = 'madre_comunitaria'";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario madre = new Usuario();
                madre.setIdUsuario(rs.getInt("id_usuario"));
                madre.setDocumento(rs.getString("documento"));
                madre.setNombres(rs.getString("nombres"));
                madre.setApellidos(rs.getString("apellidos"));
                madre.setCorreo(rs.getString("correo"));
                madre.setTelefono(rs.getString("telefono"));
                madres.add(madre);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return madres;
    }

    // ========================
    // Crear madre comunitaria
    // ========================
    public boolean crearMadre(Usuario madre, String passwordPlano) {
        String sql = "INSERT INTO usuarios (documento, nombres, apellidos, correo, direccion, telefono, password_hash, rol_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, SHA2(?,256), (SELECT id_rol FROM roles WHERE nombre_rol='madre_comunitaria'))";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, madre.getDocumento());
            ps.setString(2, madre.getNombres());
            ps.setString(3, madre.getApellidos());
            ps.setString(4, madre.getCorreo());
            ps.setString(5, madre.getDireccion());
            ps.setString(6, madre.getTelefono());
            ps.setString(7, passwordPlano);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================
    // Actualizar madre comunitaria
    // ========================
    public boolean actualizarMadre(Usuario madre) {
        String sql = "UPDATE usuarios SET nombres=?, apellidos=?, correo=?, direccion=?, telefono=? WHERE id_usuario=?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, madre.getNombres());
            ps.setString(2, madre.getApellidos());
            ps.setString(3, madre.getCorreo());
            ps.setString(4, madre.getDireccion());
            ps.setString(5, madre.getTelefono());
            ps.setInt(6, madre.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================
    // Eliminar madre comunitaria
    // ========================
    public boolean eliminarMadre(int idMadre) {
        String sql = "DELETE FROM usuarios WHERE id_usuario=?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMadre);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    // =========================
// UsuarioDAO - Métodos para PADRE
// =========================

/** Obtiene el id_usuario si ya existe un usuario con ese documento y rol 'padre'. */


/** Crea un usuario con rol 'padre' y devuelve el id generado. También crea el registro en la tabla padres. */
public PadreCreado crearPadreBasico(Usuario padre) {
    Integer rolPadreId = obtenerRolId("padre");
    if (rolPadreId == null) {
        Logger.getLogger(UsuarioDAO.class.getName())
              .severe("No existe el rol 'padre' en la tabla roles.");
        return null;
    }

    String sqlUsuario = "INSERT INTO usuarios (documento, nombres, apellidos, correo, direccion, telefono, password_hash, rol_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    // contraseña por defecto = SHA-256(documento)
    String passHash = sha256(padre.getDocumento());

    try (Connection con = ConDB.getConnection();
         PreparedStatement psUsuario = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {

        psUsuario.setLong(1, Long.parseLong(padre.getDocumento()));
        psUsuario.setString(2, padre.getNombres());
        psUsuario.setString(3, padre.getApellidos());
        psUsuario.setString(4, padre.getCorreo());
        psUsuario.setString(5, padre.getDireccion());
        psUsuario.setString(6, padre.getTelefono());
        psUsuario.setString(7, passHash);
        psUsuario.setInt(8, rolPadreId);

        int filas = psUsuario.executeUpdate();
        if (filas > 0) {
            try (ResultSet rs = psUsuario.getGeneratedKeys()) {
                if (rs.next()) {
                    int nuevoUsuarioId = rs.getInt(1);

                    // crear fila en 'padres' referenciando al usuario recién creado
                    String sqlPadre = "INSERT INTO padres (usuario_id) VALUES (?)";
                    try (PreparedStatement psPadre = con.prepareStatement(sqlPadre, Statement.RETURN_GENERATED_KEYS)) {
                        psPadre.setInt(1, nuevoUsuarioId);
                        psPadre.executeUpdate();

                        try (ResultSet rsPadre = psPadre.getGeneratedKeys()) {
                            if (rsPadre.next()) {
                                int nuevoPadreId = rsPadre.getInt(1);
                                return new PadreCreado(nuevoUsuarioId, nuevoPadreId);
                            }
                        }
                    }
                }
            }
        }
    } catch (SQLException e) {
        Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, "Error al crear padre", e);
    }
    return null;
}


/** Devuelve id_rol según nombre_rol. */
public Integer obtenerRolId(String nombreRol) {
    String sql = "SELECT id_rol FROM roles WHERE nombre_rol = ?";
    try (Connection con = ConDB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nombreRol);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
    } catch (SQLException e) {
        Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, "Error al obtener rol_id", e);
    }
    return null;
}

/** SHA-256 en Java (hex). */
    private String sha256(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No se pudo calcular SHA-256", e);
        }
    }

    public void actualizarDatosPadre(Usuario usuario, String passwordHash) throws SQLException {
        if (usuario.getIdUsuario() == 0) {
            throw new SQLException("El usuario debe tener un id válido para actualizarse");
        }

        Integer rolPadreId = obtenerRolId("padre");
        if (rolPadreId == null) {
            throw new SQLException("No existe el rol padre en la base de datos");
        }

        String sql = "UPDATE usuarios SET nombres = ?, apellidos = ?, correo = ?, direccion = ?, telefono = ?, " +
                     "password_hash = ?, rol_id = ? WHERE id_usuario = ?";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombres());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getDireccion());
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, passwordHash);
            ps.setInt(7, rolPadreId);
            ps.setInt(8, usuario.getIdUsuario());

            ps.executeUpdate();
        }
    }

 public int insertPadre(Usuario u) throws SQLException {
    int rolPadreId = obtenerRolId("padre");
    if (rolPadreId == 0) throw new SQLException("No existe el rol padre");

    String sql = "INSERT INTO usuarios (documento, nombres, apellidos, correo, direccion, telefono, password_hash, rol_id) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = ConDB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setLong(1, Long.parseLong(u.getDocumento()));
        ps.setString(2, u.getNombres());
        ps.setString(3, u.getApellidos());
        ps.setString(4, u.getCorreo());
        ps.setString(5, u.getDireccion());
        ps.setString(6, u.getTelefono());
        ps.setString(7, u.getPasswordHash()); // ya viene con SHA-256 desde el bean
        ps.setInt(8, rolPadreId);

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) return rs.getInt(1);
        }
    }
    throw new SQLException("No se pudo insertar usuario padre");
}



   public Usuario findByDocumento(long documento) {
    String sql = "SELECT * FROM usuarios WHERE documento = ?";
    try (Connection con = ConDB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setLong(1, documento);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setDocumento(rs.getString("documento"));
                u.setNombres(rs.getString("nombres"));
                u.setApellidos(rs.getString("apellidos"));
                u.setCorreo(rs.getString("correo"));
                u.setDireccion(rs.getString("direccion"));
                u.setTelefono(rs.getString("telefono"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setRolId(rs.getInt("rol_id"));
                return u;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

    public Usuario findById(int idUsuario) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setDocumento(rs.getString("documento"));
                    u.setNombres(rs.getString("nombres"));
                    u.setApellidos(rs.getString("apellidos"));
                    u.setCorreo(rs.getString("correo"));
                    u.setDireccion(rs.getString("direccion"));
                    u.setTelefono(rs.getString("telefono"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setRolId(rs.getInt("rol_id"));
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

public List<String> obtenerCorreosPadresPorHogar(int hogarId) {
    List<String> correos = new ArrayList<>();
    String sql = "SELECT u.correo " +
                 "FROM usuarios u " +
                 "JOIN padres p ON p.usuario_id = u.id_usuario " +
                 "JOIN hogares h ON h.id_hogar = p.hogar_id " +
                 "WHERE u.rol_id = ? AND h.id_hogar = ?";

    try (Connection con = ConDB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, obtenerRolId("padre")); // rol 3
        ps.setInt(2, hogarId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                correos.add(rs.getString("correo"));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return correos;
}

}
