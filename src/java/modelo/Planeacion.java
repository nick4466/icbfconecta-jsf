package modelo;

import java.io.Serializable;
import java.util.Date;

public class Planeacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Date fecha;
    private String nombreActividad;
    private String intencionalidadPedagogica;
    private String materialesUtilizar;
    private String ambientacion;
    private String actividadInicio;
    private String desarrollo;
    private String cierre;
    private String documentacion;
    private String observacion;
    private int hogarId;

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getNombreActividad() { return nombreActividad; }
    public void setNombreActividad(String nombreActividad) { this.nombreActividad = nombreActividad; }

    public String getIntencionalidadPedagogica() { return intencionalidadPedagogica; }
    public void setIntencionalidadPedagogica(String intencionalidadPedagogica) { this.intencionalidadPedagogica = intencionalidadPedagogica; }

    public String getMaterialesUtilizar() { return materialesUtilizar; }
    public void setMaterialesUtilizar(String materialesUtilizar) { this.materialesUtilizar = materialesUtilizar; }

    public String getAmbientacion() { return ambientacion; }
    public void setAmbientacion(String ambientacion) { this.ambientacion = ambientacion; }

    public String getActividadInicio() { return actividadInicio; }
    public void setActividadInicio(String actividadInicio) { this.actividadInicio = actividadInicio; }

    public String getDesarrollo() { return desarrollo; }
    public void setDesarrollo(String desarrollo) { this.desarrollo = desarrollo; }

    public String getCierre() { return cierre; }
    public void setCierre(String cierre) { this.cierre = cierre; }

    public String getDocumentacion() { return documentacion; }
    public void setDocumentacion(String documentacion) { this.documentacion = documentacion; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public int getHogarId() { return hogarId; }
    public void setHogarId(int hogarId) { this.hogarId = hogarId; }

    @Override
    public String toString() {
        return "Planeacion{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", nombreActividad='" + nombreActividad + '\'' +
                ", intencionalidadPedagogica='" + intencionalidadPedagogica + '\'' +
                ", materialesUtilizar='" + materialesUtilizar + '\'' +
                ", ambientacion='" + ambientacion + '\'' +
                ", actividadInicio='" + actividadInicio + '\'' +
                ", desarrollo='" + desarrollo + '\'' +
                ", cierre='" + cierre + '\'' +
                ", documentacion='" + documentacion + '\'' +
                ", observacion='" + observacion + '\'' +
                ", hogarId=" + hogarId +
                '}';
    }
}
