package modelo;

import java.util.Date;

public class ReporteNinoDTO {

    // Niño
    private int idNino;
    private String ninoNombres;
    private String ninoApellidos;
    private Date fechaNacimiento;
    private Long ninoDocumento;
    private String genero;
    private String nacionalidad;
    private Date fechaIngreso;
    private String foto;
    private String carnetVacunacion;
    private String certificadoEps;

    // Padre
    private int idPadre;
    private int padreUsuarioId;
    private String padreNombres;
    private String padreApellidos;
    private String padreDocumento;
    private String padreCorreo;
    private String padreTelefono;
    private String padreDireccion;
    private String ocupacion;
    private Integer estrato;
    private String telEmerg;
    private String nomEmerg;
    private String situacionEcon;
    private String padrePasswordHash;

    // Hogar
    private int idHogar;
    private String nombreHogar;
    private String hogarDireccion;
    private String localidad;

    // ===== Getters y Setters =====
    public int getIdNino() { return idNino; }
    public void setIdNino(int idNino) { this.idNino = idNino; }

    public String getNinoNombres() { return ninoNombres; }
    public void setNinoNombres(String ninoNombres) { this.ninoNombres = ninoNombres; }

    public String getNinoApellidos() { return ninoApellidos; }
    public void setNinoApellidos(String ninoApellidos) { this.ninoApellidos = ninoApellidos; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Long getNinoDocumento() { return ninoDocumento; }
    public void setNinoDocumento(Long ninoDocumento) { this.ninoDocumento = ninoDocumento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public Date getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Date fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getCarnetVacunacion() { return carnetVacunacion; }
    public void setCarnetVacunacion(String carnetVacunacion) { this.carnetVacunacion = carnetVacunacion; }

    public String getCertificadoEps() { return certificadoEps; }
    public void setCertificadoEps(String certificadoEps) { this.certificadoEps = certificadoEps; }

    public int getIdPadre() { return idPadre; }
    public void setIdPadre(int idPadre) { this.idPadre = idPadre; }

    public int getPadreUsuarioId() { return padreUsuarioId; }
    public void setPadreUsuarioId(int padreUsuarioId) { this.padreUsuarioId = padreUsuarioId; }

    public String getPadreNombres() { return padreNombres; }
    public void setPadreNombres(String padreNombres) { this.padreNombres = padreNombres; }

    public String getPadreApellidos() { return padreApellidos; }
    public void setPadreApellidos(String padreApellidos) { this.padreApellidos = padreApellidos; }

    public String getPadreDocumento() { return padreDocumento; }
    public void setPadreDocumento(String padreDocumento) { this.padreDocumento = padreDocumento; }

    public String getPadreCorreo() { return padreCorreo; }
    public void setPadreCorreo(String padreCorreo) { this.padreCorreo = padreCorreo; }

    public String getPadreTelefono() { return padreTelefono; }
    public void setPadreTelefono(String padreTelefono) { this.padreTelefono = padreTelefono; }

    public String getPadreDireccion() { return padreDireccion; }
    public void setPadreDireccion(String padreDireccion) { this.padreDireccion = padreDireccion; }

    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }

    public Integer getEstrato() { return estrato; }
    public void setEstrato(Integer estrato) { this.estrato = estrato; }

    public String getTelEmerg() { return telEmerg; }
    public void setTelEmerg(String telEmerg) { this.telEmerg = telEmerg; }

    public String getNomEmerg() { return nomEmerg; }
    public void setNomEmerg(String nomEmerg) { this.nomEmerg = nomEmerg; }

    public String getSituacionEcon() { return situacionEcon; }
    public void setSituacionEcon(String situacionEcon) { this.situacionEcon = situacionEcon; }

    public String getPadrePasswordHash() { return padrePasswordHash; }
    public void setPadrePasswordHash(String padrePasswordHash) { this.padrePasswordHash = padrePasswordHash; }

    public int getIdHogar() { return idHogar; }
    public void setIdHogar(int idHogar) { this.idHogar = idHogar; }

    public String getNombreHogar() { return nombreHogar; }
    public void setNombreHogar(String nombreHogar) { this.nombreHogar = nombreHogar; }

    public String getHogarDireccion() { return hogarDireccion; }
    public void setHogarDireccion(String hogarDireccion) { this.hogarDireccion = hogarDireccion; }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
}
