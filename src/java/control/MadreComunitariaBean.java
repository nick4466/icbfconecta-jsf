package control;

import dao.UsuarioDAO;
import modelo.Usuario;

import java.io.Serializable;
import java.util.List;
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
        FacesContext ctx = FacesContext.getCurrentInstance();
        System.out.println("[LOG] Intentando guardar madre: " + madre.getDocumento() + " - " + madre.getNombres() + " " + madre.getApellidos());
        if (usuarioDAO.crearMadre(madre, passwordPlano)) {
            System.out.println("[LOG] Madre registrada correctamente");
            ctx.addMessage(null, new javax.faces.application.FacesMessage("Madre registrada correctamente"));
            madre = new Usuario();
            passwordPlano = "";
            return "listarMadres?faces-redirect=true";
        }
        System.out.println("[LOG] Error al registrar madre");
        ctx.addMessage(null, new javax.faces.application.FacesMessage(javax.faces.application.FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar la madre"));
        return null;
    }

    // Editar madre
    public String editarMadre(Usuario m) {
        System.out.println("[LOG] Editando madre: " + m.getIdUsuario());
        this.madre = m;
        return "editarMadre?faces-redirect=true";
    }

    // Actualizar madre
    public String actualizarMadre() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        System.out.println("[LOG] Actualizando madre: " + madre.getIdUsuario());
        if (usuarioDAO.actualizarMadre(madre)) {
            System.out.println("[LOG] Madre actualizada correctamente");
            ctx.addMessage(null, new javax.faces.application.FacesMessage("Madre actualizada correctamente"));
            madre = new Usuario();
            return "listarMadres?faces-redirect=true";
        }
        System.out.println("[LOG] Error al actualizar madre");
        ctx.addMessage(null, new javax.faces.application.FacesMessage(javax.faces.application.FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar la madre"));
        return null;
    }

    // Eliminar madre
    public String eliminarMadre(int id) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        System.out.println("[LOG] Eliminando madre: " + id);
        if (usuarioDAO.eliminarMadre(id)) {
            ctx.addMessage(null, new javax.faces.application.FacesMessage("Madre eliminada correctamente"));
        } else {
            ctx.addMessage(null, new javax.faces.application.FacesMessage(javax.faces.application.FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar la madre"));
        }
        return "listarMadres?faces-redirect=true";
    }
}
