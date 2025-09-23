package dao;

import modelo.Planeacion;
import control.ConDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaneacionDAO {

    // ================== LISTAR ==================
    public List<Planeacion> listarPlaneacionesPorHogar(int hogarId) {
        List<Planeacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM planeaciones WHERE hogar_id = ? ORDER BY fecha DESC";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hogarId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ================== OBTENER POR ID ==================
    public Planeacion obtenerPorId(int id) {
        String sql = "SELECT * FROM planeaciones WHERE id = ?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ================== CREAR ==================
    public boolean crearPlaneacion(Planeacion p) {
        String sql = "INSERT INTO planeaciones (fecha, nombre_actividad, intencionalidad_pedagogica, materiales_utilizar, ambientacion, actividad_inicio, desarrollo, cierre, documentacion, observacion, hogar_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(p.getFecha().getTime()));
            ps.setString(2, p.getNombreActividad());
            ps.setString(3, p.getIntencionalidadPedagogica());
            ps.setString(4, p.getMaterialesUtilizar());
            ps.setString(5, p.getAmbientacion());
            ps.setString(6, p.getActividadInicio());
            ps.setString(7, p.getDesarrollo());
            ps.setString(8, p.getCierre());
            ps.setString(9, p.getDocumentacion());
            ps.setString(10, p.getObservacion());
            ps.setInt(11, p.getHogarId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================== ACTUALIZAR ==================
    public boolean actualizarPlaneacion(Planeacion p) {
        String sql = "UPDATE planeaciones SET fecha=?, nombre_actividad=?, intencionalidad_pedagogica=?, materiales_utilizar=?, ambientacion=?, actividad_inicio=?, desarrollo=?, cierre=?, documentacion=?, observacion=? WHERE id=? AND hogar_id=?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(p.getFecha().getTime()));
            ps.setString(2, p.getNombreActividad());
            ps.setString(3, p.getIntencionalidadPedagogica());
            ps.setString(4, p.getMaterialesUtilizar());
            ps.setString(5, p.getAmbientacion());
            ps.setString(6, p.getActividadInicio());
            ps.setString(7, p.getDesarrollo());
            ps.setString(8, p.getCierre());
            ps.setString(9, p.getDocumentacion());
            ps.setString(10, p.getObservacion());
            ps.setInt(11, p.getId());
            ps.setInt(12, p.getHogarId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================== ELIMINAR ==================
    public boolean eliminarPlaneacion(int id) {
        String sql = "DELETE FROM planeaciones WHERE id=?";
        try (Connection con = ConDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================== MAPEO ==================
    private Planeacion mapear(ResultSet rs) throws SQLException {
        Planeacion p = new Planeacion();
        p.setId(rs.getInt("id"));
        p.setFecha(rs.getDate("fecha"));
        p.setNombreActividad(rs.getString("nombre_actividad"));
        p.setIntencionalidadPedagogica(rs.getString("intencionalidad_pedagogica"));
        p.setMaterialesUtilizar(rs.getString("materiales_utilizar"));
        p.setAmbientacion(rs.getString("ambientacion"));
        p.setActividadInicio(rs.getString("actividad_inicio"));
        p.setDesarrollo(rs.getString("desarrollo"));
        p.setCierre(rs.getString("cierre"));
        p.setDocumentacion(rs.getString("documentacion"));
        p.setObservacion(rs.getString("observacion"));
        p.setHogarId(rs.getInt("hogar_id"));
        return p;
    }
}
