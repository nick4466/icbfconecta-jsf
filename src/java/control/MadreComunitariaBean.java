package control;

import dao.UsuarioDAO;
import modelo.Usuario;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "madreBean")
@SessionScoped
public class MadreComunitariaBean implements Serializable {

    private Usuario madre = new Usuario();
    private String passwordPlano; // para registrar nuevas madres
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Getter y setter
    public Usuario getMadre() {
        return madre;
    }

    public void setMadre(Usuario madre) {
        this.madre = madre;
    }

    public String getPasswordPlano() {
        return passwordPlano;
    }

    public void setPasswordPlano(String passwordPlano) {
        this.passwordPlano = passwordPlano;
    }

    // Listar todas las madres
    public List<Usuario> getMadres() {
        return usuarioDAO.obtenerMadresComunitarias();
    }

    // Crear nueva madre
    public String guardarMadre() {
        if (usuarioDAO.crearMadre(madre, passwordPlano)) {
            madre = new Usuario();
            passwordPlano = "";
            return "listarMadres?faces-redirect=true";
        }
        return null;
    }

    // Editar madre
    public String editarMadre(Usuario m) {
        this.madre = m;
        return "editarMadre?faces-redirect=true";
    }

    // Actualizar madre
    public String actualizarMadre() {
        if (usuarioDAO.actualizarMadre(madre)) {
            madre = new Usuario();
            return "listarMadres?faces-redirect=true";
        }
        return null;
    }

    // Eliminar madre
    public String eliminarMadre(int id) {
        usuarioDAO.eliminarMadre(id);
        return "listarMadres?faces-redirect=true";
    }
}
