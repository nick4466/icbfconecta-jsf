package dao;

import control.ConDB;
import modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CorreoDAO {

    public List<Usuario> listarCorreosPadresPorHogar(int hogarId) {
    List<Usuario> padres = new ArrayList<>();
    String sql = "SELECT DISTINCT u.id_usuario, u.nombres, u.apellidos, u.correo " +
                 "FROM ninos n " +
                 "INNER JOIN padres p ON n.padre_id = p.id_padre " +
                 "INNER JOIN usuarios u ON p.usuario_id = u.id_usuario " +
                 "WHERE n.hogar_id = ? AND u.rol_id = 3";

    try (Connection con = ConDB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, hogarId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombres(rs.getString("nombres"));
                u.setApellidos(rs.getString("apellidos"));
                u.setCorreo(rs.getString("correo"));
                padres.add(u);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return padres;
}

    
    public Integer obtenerHogarMadre(int idUsuarioMadre) {
    String sql = "SELECT hogar_id FROM madres WHERE usuario_id = ?";
    try (Connection con = ConDB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, idUsuarioMadre);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("hogar_id");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

}
