package control;

import dao.UsuarioDAO;
import modelo.Usuario;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String documento;
    private String password;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // =========================
    // Getters y setters
    // =========================
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // =========================
    // Login
    // =========================
    public String login() {
        Usuario usuario = usuarioDAO.validarLogin(documento, password);

        if (usuario != null) {
            // Guardar en sessionBean
            SessionBean session = (SessionBean) FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .get("sessionBean");

            if (session == null) {
                session = new SessionBean();
                FacesContext.getCurrentInstance().getExternalContext()
                        .getSessionMap().put("sessionBean", session);
            }

            session.setUsuarioLogueado(usuario);

            // 🚦 Redirección según rol (rol es un String en Usuario.java)
            String rol = usuario.getRol();

            switch (rol) {
                case "administrador":
                    return "adminDashboard?faces-redirect=true";
                case "madre_comunitaria":
                    return "madreDashboard?faces-redirect=true";
                case "padre":
                    return "padreDashboard?faces-redirect=true";
                default:
                    // Si no hay rol válido, vuelve al login
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Rol no autorizado", null));
                    return "index?faces-redirect=true";
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Credenciales inválidas", null));
            return null;
        }
    }

    // =========================
    // Logout
    // =========================
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index.xhtml?faces-redirect=true";
    }
}
