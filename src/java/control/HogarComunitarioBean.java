package control;

import dao.HogarComunitarioDAO;
import dao.UsuarioDAO;
import modelo.HogarComunitario;
import modelo.Usuario;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "hogarBean")
@SessionScoped
public class HogarComunitarioBean implements Serializable {

    private HogarComunitario hogar = new HogarComunitario();
    private final HogarComunitarioDAO hogarDAO = new HogarComunitarioDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Getter y setter
    public HogarComunitario getHogar() {
        return hogar;
    }

    public void setHogar(HogarComunitario hogar) {
        this.hogar = hogar;
    }

    // Guardar hogar comunitario
    public String guardarHogar() {
        if (hogarDAO.crearHogar(hogar)) {
            // Redirigir al dashboard después de guardar
            return "adminDashboard?faces-redirect=true";
        } else {
            return null; // se queda en la misma página si falla
        }
    }

    // Obtener lista de madres comunitarias para el menú desplegable
    public List<Usuario> getMadresComunitarias() {
        return usuarioDAO.obtenerMadresComunitarias();
    }

    // Listar hogares
    public List<HogarComunitario> getHogares() {
        return hogarDAO.listarHogares();
    }

    // Editar hogar
    public String editarHogar(HogarComunitario h) {
        this.hogar = h; // cargar datos al bean
        return "editarHogar?faces-redirect=true";
    }

    // Actualizar hogar
    public String actualizarHogar() {
        if (hogarDAO.actualizarHogar(hogar)) {
            return "listarHogares?faces-redirect=true";
        } else {
            return null;
        }
    }

    // Eliminar hogar
    public String eliminarHogar(int id) {
        hogarDAO.eliminarHogar(id);
        return "listarHogares?faces-redirect=true";
    }
}
