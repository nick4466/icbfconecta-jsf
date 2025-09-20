package control;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import modelo.Usuario;

@ManagedBean(name = "sessionBean")
@SessionScoped
public class SessionBean implements Serializable {
    private Usuario usuarioLogueado;

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    public String logout() {
        usuarioLogueado = null;
        return "index.xhtml?faces-redirect=true";
    }
    public String verificarAcceso(String rolRequerido) {
    if (usuarioLogueado == null || usuarioLogueado.getRol() == null
            || !usuarioLogueado.getRol().equals(rolRequerido)) {
        // Redirige al login si no hay usuario o el rol no coincide
        return "index?faces-redirect=true";
    }
    return null; // acceso permitido
}

}



