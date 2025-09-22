package control;


import dao.AsistenciaDAO;
import dao.NinoDAO;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import modelo.Asistencia;
import modelo.Nino;

// AsistenciaBean.java
@ManagedBean
@SessionScoped
public class AsistenciaBean implements Serializable {

    private Asistencia asistencia = new Asistencia();
    private AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private NinoDAO ninoDAO = new NinoDAO();

    // Lista de asistencias
    public List<Asistencia> getLista() {
        return asistenciaDAO.listarConNombres();
    }

    // Lista de niños para el combo
    public List<Nino> getNinos() {
        return ninoDAO.listar();
    }

    // Guardar
    public String guardar() {
        asistencia.setFecha(new Date(System.currentTimeMillis()));
        asistenciaDAO.insertar(asistencia);
        asistencia = new Asistencia(); // limpiar
        return "listarAsistencia?faces-redirect=true";
    }

    // ==========================
    // Editar
    // ==========================
    public String editar(Asistencia a) {
        this.asistencia = a; // cargamos la asistencia seleccionada
        return "editarAsistencia?faces-redirect=true";
    }

    public String actualizar() {
        asistenciaDAO.actualizar(asistencia);
        asistencia = new Asistencia(); // limpiar
        return "listarAsistencia?faces-redirect=true";
    }

    // ==========================
    // Eliminar
    // ==========================
    public String eliminar(int idAsistencia) {
        asistenciaDAO.eliminar(idAsistencia);
        return "listarAsistencia?faces-redirect=true";
    }

    // Getters y setters
    public Asistencia getAsistencia() { return asistencia; }
    public void setAsistencia(Asistencia asistencia) { this.asistencia = asistencia; }
}
