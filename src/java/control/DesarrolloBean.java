package control;

import dao.DesarrolloDAO;
import modelo.Desarrollo;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@ManagedBean(name = "desarrolloBean")
@ViewScoped
public class DesarrolloBean implements Serializable {
    private Desarrollo desarrollo;
    private List<Desarrollo> listaDesarrollo;
    private Desarrollo detalle;
    private DesarrolloDAO dao;

    @PostConstruct
    public void init() {
        dao = new DesarrolloDAO();
        desarrollo = new Desarrollo();
        listar();
    }

    public void listar() {
        try {
            listaDesarrollo = dao.listar();
            if (listaDesarrollo == null) {
                System.out.println("[DEBUG] listaDesarrollo es null");
            } else {
                System.out.println("[DEBUG] listaDesarrollo size: " + listaDesarrollo.size());
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Excepción en listar(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void prepararNuevo() {
        desarrollo = new Desarrollo();
    }

    public void verDetalle(Desarrollo d) {
        detalle = d;
    }

    public void cargarParaEditar(Desarrollo d) {
        this.desarrollo = d;
    }

    public void guardar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            // DEPURACIÓN: Mostrar los datos recibidos
            System.out.println("---- DATOS DEL FORMULARIO ----");
            System.out.println("idNino: " + desarrollo.getIdNino());
            System.out.println("fechaFinMes: " + desarrollo.getFechaFinMes());
            System.out.println("dimensionCognitiva: " + desarrollo.getDimensionCognitiva());
            System.out.println("dimensionComunicativa: " + desarrollo.getDimensionComunicativa());
            System.out.println("dimensionSocioAfectiva: " + desarrollo.getDimensionSocioAfectiva());
            System.out.println("dimensionCorporal: " + desarrollo.getDimensionCorporal());

            if (desarrollo.getIdNino() == 0 || desarrollo.getFechaFinMes() == null ||
                desarrollo.getDimensionCognitiva() == null || desarrollo.getDimensionComunicativa() == null ||
                desarrollo.getDimensionSocioAfectiva() == null || desarrollo.getDimensionCorporal() == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Todos los campos son obligatorios."));
                System.out.println("[ERROR] Algún campo obligatorio está vacío o nulo");
                return;
            }
            // Conversión segura de fecha
            if (!(desarrollo.getFechaFinMes() instanceof java.sql.Date)) {
                java.util.Date utilDate = desarrollo.getFechaFinMes();
                desarrollo.setFechaFinMes(new java.sql.Date(utilDate.getTime()));
            }
            if (desarrollo.getIdDesarrollo() == 0) {
                dao.insertar(desarrollo);
                context.addMessage(null, new FacesMessage("Registrado con éxito"));
            } else {
                dao.actualizar(desarrollo);
                context.addMessage(null, new FacesMessage("Actualizado con éxito"));
            }
            desarrollo = new Desarrollo();
            listar();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            e.printStackTrace();
        }
    }

    public void eliminar(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            dao.eliminar(id);
            listar();
            context.addMessage(null, new FacesMessage("Eliminado con éxito"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    // Getters y Setters
    public Desarrollo getDesarrollo() { return desarrollo; }
    public void setDesarrollo(Desarrollo desarrollo) { this.desarrollo = desarrollo; }
    public List<Desarrollo> getListaDesarrollo() { return listaDesarrollo; }
    public void setListaDesarrollo(List<Desarrollo> listaDesarrollo) { this.listaDesarrollo = listaDesarrollo; }
    public Desarrollo getDetalle() { return detalle; }
    public void setDetalle(Desarrollo detalle) { this.detalle = detalle; }
}
