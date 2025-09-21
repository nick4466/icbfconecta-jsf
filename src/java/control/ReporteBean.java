package control;

import dao.ReporteDAO;
import modelo.ReporteNinoDTO;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
        cargarSeleccionadoDesdeContexto();
        cargarLista();
    }

    // =========================
    // Métodos de acción
    // =========================

    public void cargarLista() {
        listaReportes = reporteDAO.findAll();
    }

    public String verDetalle(int idNino) {
        ReporteNinoDTO dto = reporteDAO.findById(idNino);
        if (dto != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                ExternalContext external = context.getExternalContext();
                if (external != null) {
                    external.getFlash().put("reporteSeleccionado", dto);
                }
            }
            seleccionado = dto;
            return "ReporteDetalle?faces-redirect=true";
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

    private void cargarSeleccionadoDesdeContexto() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            return;
        }

        ExternalContext external = context.getExternalContext();
        if (external == null) {
            return;
        }

        Object flashValue = external.getFlash().get("reporteSeleccionado");
        if (flashValue instanceof ReporteNinoDTO) {
            seleccionado = (ReporteNinoDTO) flashValue;
            return;
        }

        Map<String, String> params = external.getRequestParameterMap();
        if (params == null) {
            return;
        }

        String idParam = params.get("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            return;
        }

        try {
            int id = Integer.parseInt(idParam.trim());
            seleccionado = reporteDAO.findById(id);
        } catch (NumberFormatException ignored) {
            // parámetro inválido, se deja seleccionado en null
        }
    }
}
