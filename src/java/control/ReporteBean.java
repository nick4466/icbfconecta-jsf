package control;

import dao.ReporteDAO;
import modelo.ReporteNinoDTO;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "reporteBean")
@ViewScoped
public class ReporteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ReporteNinoDTO> listaReportes; // todos los niños con info completa
    private ReporteNinoDTO seleccionado;        // niño seleccionado para ver detalle

    private ReporteDAO reporteDAO;

    // Constructor vacío requerido por JSF
    public ReporteBean() {
    }

    @PostConstruct
    public void init() {
        reporteDAO = new ReporteDAO();
        cargarLista();
    }

    // =========================
    // Métodos de acción
    // =========================

    public void cargarLista() {
        listaReportes = reporteDAO.findAll();
    }

    public String verDetalle(int idNino) {
        seleccionado = reporteDAO.findById(idNino);
        if (seleccionado != null) {
            // navega a la vista detalle (crear reporteDetalle.xhtml)
            return "reporteDetalle?faces-redirect=true";
        }
        return null;
    }

    public void refrescar() {
        cargarLista();
    }

    // =========================
    // Getters / Setters
    // =========================

    public List<ReporteNinoDTO> getListaReportes() {
        return listaReportes;
    }

    public void setListaReportes(List<ReporteNinoDTO> listaReportes) {
        this.listaReportes = listaReportes;
    }

    public ReporteNinoDTO getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(ReporteNinoDTO seleccionado) {
        this.seleccionado = seleccionado;
    }
}
