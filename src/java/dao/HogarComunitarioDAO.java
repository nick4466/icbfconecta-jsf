package dao;

import modelo.HogarComunitario;
import control.ConDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HogarComunitarioDAO {

    // Crear hogar
    public boolean crearHogar(HogarComunitario hogar) {
        String sql = "INSERT INTO hogares_comunitarios " +
                     "(nombre_hogar, direccion, localidad, capacidad_maxima, estado, madre_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hogar.getNombreHogar());
            ps.setString(2, hogar.getDireccion());
            ps.setString(3, hogar.getLocalidad());
            ps.setInt(4, hogar.getCapacidadMaxima());
            ps.setString(5, hogar.getEstado());
            ps.setInt(6, hogar.getMadreId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Listar todos los hogares
    public List<HogarComunitario> listarHogares() {
        List<HogarComunitario> lista = new ArrayList<>();
        String sql = "SELECT * FROM hogares_comunitarios";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearHogar(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 🔹 Listar hogares activos (para combo dinámico en matrícula)
    public List<HogarComunitario> listarActivos() {
        List<HogarComunitario> lista = new ArrayList<>();
        String sql = "SELECT * FROM hogares_comunitarios WHERE estado = 'activo'";

        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearHogar(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Actualizar hogar
    public boolean actualizarHogar(HogarComunitario hogar) {
        String sql = "UPDATE hogares_comunitarios " +
                     "SET nombre_hogar=?, direccion=?, localidad=?, capacidad_maxima=?, estado=?, madre_id=? " +
                     "WHERE id_hogar=?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hogar.getNombreHogar());
            ps.setString(2, hogar.getDireccion());
            ps.setString(3, hogar.getLocalidad());
            ps.setInt(4, hogar.getCapacidadMaxima());
            ps.setString(5, hogar.getEstado());
            ps.setInt(6, hogar.getMadreId());
            ps.setInt(7, hogar.getIdHogar());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar hogar
    public boolean eliminarHogar(int idHogar) {
        String sql = "DELETE FROM hogares_comunitarios WHERE id_hogar=?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idHogar);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método privado para mapear resultset
    private HogarComunitario mapearHogar(ResultSet rs) throws Exception {
        HogarComunitario h = new HogarComunitario();
        h.setIdHogar(rs.getInt("id_hogar"));
        h.setNombreHogar(rs.getString("nombre_hogar"));
        h.setDireccion(rs.getString("direccion"));
        h.setLocalidad(rs.getString("localidad"));
        h.setCapacidadMaxima(rs.getInt("capacidad_maxima"));
        h.setEstado(rs.getString("estado"));
        h.setMadreId(rs.getInt("madre_id"));
        return h;
    }
}
