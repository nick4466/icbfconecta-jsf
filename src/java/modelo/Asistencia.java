package modelo;

import java.sql.Date;

public class Asistencia {
    private int idAsistencia;
    private int idNino;
    private Date fecha;
    private String estado;

    private String nombres;
    private String apellidos;

    private String madreNombres;
    private String madreApellidos;
    private String nombreHogar;

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

    public String getMadreNombres() { return madreNombres; }
    public void setMadreNombres(String madreNombres) { this.madreNombres = madreNombres; }

    public String getMadreApellidos() { return madreApellidos; }
    public void setMadreApellidos(String madreApellidos) { this.madreApellidos = madreApellidos; }

    public String getNombreHogar() { return nombreHogar; }
    public void setNombreHogar(String nombreHogar) { this.nombreHogar = nombreHogar; }

    public String getMadreNombreCompleto() {
        String nombresMadre = madreNombres == null ? "" : madreNombres.trim();
        String apellidosMadre = madreApellidos == null ? "" : madreApellidos.trim();
        String nombreCompleto = (nombresMadre + " " + apellidosMadre).trim();
        return nombreCompleto.isEmpty() ? null : nombreCompleto;
    }

    public String getNinoNombreCompleto() {
        String nombresNino = nombres == null ? "" : nombres.trim();
        String apellidosNino = apellidos == null ? "" : apellidos.trim();
        String nombreCompleto = (nombresNino + " " + apellidosNino).trim();
        return nombreCompleto.isEmpty() ? null : nombreCompleto;
    }
}
