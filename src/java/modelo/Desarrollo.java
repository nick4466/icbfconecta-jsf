package modelo;
import java.io.Serializable;
import java.util.Date;
import java.sql.Timestamp;

public class Desarrollo implements Serializable {
    private int idDesarrollo;
    private int idNino;
    private String nombreNino; // solo para mostrar
    private Date fechaFinMes;
    private String dimensionCognitiva;
    private String dimensionComunicativa;
    private String dimensionSocioAfectiva;
    private String dimensionCorporal;
    private Timestamp fechaRegistro;

    public Desarrollo() {}

    // Getters y setters
    public int getIdDesarrollo() { return idDesarrollo; }
    public void setIdDesarrollo(int idDesarrollo) { this.idDesarrollo = idDesarrollo; }

    public int getIdNino() { return idNino; }
    public void setIdNino(int idNino) { this.idNino = idNino; }

    public String getNombreNino() { return nombreNino; }
    public void setNombreNino(String nombreNino) { this.nombreNino = nombreNino; }

    public Date getFechaFinMes() { return fechaFinMes; }
    public void setFechaFinMes(Date fechaFinMes) { this.fechaFinMes = fechaFinMes; }

    public String getDimensionCognitiva() { return dimensionCognitiva; }
    public void setDimensionCognitiva(String dimensionCognitiva) { this.dimensionCognitiva = dimensionCognitiva; }

    public String getDimensionComunicativa() { return dimensionComunicativa; }
    public void setDimensionComunicativa(String dimensionComunicativa) { this.dimensionComunicativa = dimensionComunicativa; }

    public String getDimensionSocioAfectiva() { return dimensionSocioAfectiva; }
    public void setDimensionSocioAfectiva(String dimensionSocioAfectiva) { this.dimensionSocioAfectiva = dimensionSocioAfectiva; }

    public String getDimensionCorporal() { return dimensionCorporal; }
    public void setDimensionCorporal(String dimensionCorporal) { this.dimensionCorporal = dimensionCorporal; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}

