package control;

import dao.UsuarioDAO;
import modelo.Usuario;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

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
        FacesContext context = FacesContext.getCurrentInstance();

        if (usuarioDAO.crearMadre(madre, passwordPlano)) {
            madre = new Usuario();
            passwordPlano = null;
            return "listarMadres?faces-redirect=true";
        }

        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "No se pudo registrar la madre comunitaria",
                "Verifica que el documento y el correo no estén registrados y que la contraseña sea válida."));
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
