package modelo;

import java.sql.Date;

public class Asistencia {
    private int idAsistencia;
    private int idNino;
    private Date fecha;
    private String estado;
    
    private String nombres;
    private String apellidos;
    
    private Nino nino; //aqui para intentar relacionar al niño 

    // Getters y Setters
    public int getIdAsistencia() {
        return idAsistencia;
    }
    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public int getIdNino() {
        return idNino;
    }
    public void setIdNino(int idNino) {
        this.idNino = idNino;
    }

    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public Nino getNino() { return nino; }
    public void setNino(Nino nino) { this.nino = nino; }
    
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
}
