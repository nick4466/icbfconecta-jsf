package control;

import dao.HogarComunitarioDAO;
import dao.PlaneacionDAO;
import modelo.HogarComunitario;
import modelo.Planeacion;
import modelo.Usuario;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "planeacionBean")
@ViewScoped
public class PlaneacionBean implements Serializable {

    private Planeacion planeacion = new Planeacion();
    private List<Planeacion> planeaciones = new ArrayList<>();
    private Planeacion planeacionSeleccionada;
    private final PlaneacionDAO dao = new PlaneacionDAO();

    private int hogarId;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @PostConstruct
    public void init() {
        if (sessionBean != null && sessionBean.getUsuarioLogueado() != null) {
            Usuario madre = sessionBean.getUsuarioLogueado();
            HogarComunitarioDAO hogarDAO = new HogarComunitarioDAO();
            HogarComunitario hogar = hogarDAO.obtenerPorMadre(madre.getIdUsuario());
            if (hogar != null) {
                hogarId = hogar.getIdHogar();
            } else {
                hogarId = 0;
            }
        } else {
            hogarId = 0;
            System.out.println("[LOG] No hay madre logueada en sesión");
        }
        listar();
    }

    public Planeacion getPlaneacion() {
        return planeacion;
    }

    public void setPlaneacion(Planeacion planeacion) {
        this.planeacion = planeacion;
    }

    public Planeacion getPlaneacionSeleccionada() {
        return planeacionSeleccionada;
    }

    public void setPlaneacionSeleccionada(Planeacion planeacionSeleccionada) {
        this.planeacionSeleccionada = planeacionSeleccionada;
    }

    public List<Planeacion> getPlaneaciones() {
        return planeaciones;
    }

    public int getHogarId() {
        return hogarId;
    }

    public void setHogarId(int hogarId) {
        this.hogarId = hogarId;
    }

    // ================== CRUD ==================

    public void listar() {
        if (hogarId == 0) {
            planeaciones = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "No se encontró hogar asociado. No se pueden mostrar planeaciones."));
            return;
        }
        planeaciones = dao.listarPlaneacionesPorHogar(hogarId);
    }

    public String guardar() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (hogarId == 0) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontró hogar asociado. No se puede registrar planeación."));
            return null;
        }
        try {
            planeacion.setHogarId(hogarId);
            boolean exito;
            if (planeacion.getId() == 0) {
                exito = dao.crearPlaneacion(planeacion);
                ctx.addMessage(null, new FacesMessage("Planeación registrada"));
            } else {
                exito = dao.actualizarPlaneacion(planeacion);
                ctx.addMessage(null, new FacesMessage("Planeación actualizada"));
            }
            if (exito) { // No se redirige, se queda en la misma página
                planeacion = new Planeacion();
                listar();
                return null; // Se queda en la misma vista, la actualización es por AJAX
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
        return null;
    }

    public void abrirFormulario(Planeacion p) {
        if (p != null) {
            // Editar: Carga la planeación existente
            this.planeacion = p;
        } else {
            // Nuevo: Prepara un objeto vacío
            this.planeacion = new Planeacion();
        }
    }

    public void verDetalle(Planeacion p) {
        this.planeacionSeleccionada = p;
    }

    public void eliminar(int id) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            dao.eliminarPlaneacion(id);
            listar();
            ctx.addMessage(null, new FacesMessage("Eliminado con éxito"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }
}
