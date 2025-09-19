package modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
/**
 * POJO que representa la tabla ninos.
 * Campos alineados con la BD conecta_icbf_v2.ninos
 */
public class Nino implements Serializable {

    private static final long serialVersionUID = 1L;

    // =========================
    // Atributos
    // =========================
    private int idNino;                  // PK AUTO_INCREMENT
    private String nombres;              // NOT NULL
    private String apellidos;            // NOT NULL
    private Date fechaNacimiento;        // NOT NULL (java.sql.Date para JDBC)
    private Long documento;              // UNIQUE, puede ser null
    private String genero;               // 'masculino','femenino','otro','no_especificado'
    private String nacionalidad;         // puede ser null
    private Date fechaIngreso;           // DEFAULT CURRENT_DATE
    private int hogarId;                 // NOT NULL FK -> hogares_comunitarios.id_hogar
    private int padreId;                 // NOT NULL FK -> usuarios.id_usuario

    // Campos para archivos (rutas en servidor)
    private String foto;                 // ruta foto del niño
    private String carnetVacunacion;     // ruta carnet de vacunación
    private String certificadoEps;       // ruta certificado EPS

    // =========================
    // Constructores
    // =========================
    public Nino() {}

    // Constructor útil para insertar (sin idNino)
    public Nino(String nombres, String apellidos, Date fechaNacimiento, Long documento,
                String genero, String nacionalidad, Date fechaIngreso,
                int hogarId, int padreId,
                String foto, String carnetVacunacion, String certificadoEps) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.documento = documento;
        this.genero = genero;
        this.nacionalidad = nacionalidad;
        this.fechaIngreso = fechaIngreso;
        this.hogarId = hogarId;
        this.padreId = padreId;
        this.foto = foto;
        this.carnetVacunacion = carnetVacunacion;
        this.certificadoEps = certificadoEps;
    }

    // Constructor completo (incluye id)
    public Nino(int idNino, String nombres, String apellidos, Date fechaNacimiento, Long documento,
                String genero, String nacionalidad, Date fechaIngreso,
                int hogarId, int padreId,
                String foto, String carnetVacunacion, String certificadoEps) {
        this(nombres, apellidos, fechaNacimiento, documento, genero, nacionalidad,
             fechaIngreso, hogarId, padreId, foto, carnetVacunacion, certificadoEps);
        this.idNino = idNino;
    }

    // =========================
    // Getters y Setters
    // =========================
    public int getIdNino() { return idNino; }
    public void setIdNino(int idNino) { this.idNino = idNino; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Long getDocumento() { return documento; }
    public void setDocumento(Long documento) { this.documento = documento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public Date getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Date fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public int getHogarId() { return hogarId; }
    public void setHogarId(int hogarId) { this.hogarId = hogarId; }

    public int getPadreId() { return padreId; }
    public void setPadreId(int padreId) { this.padreId = padreId; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getCarnetVacunacion() { return carnetVacunacion; }
    public void setCarnetVacunacion(String carnetVacunacion) { this.carnetVacunacion = carnetVacunacion; }

    public String getCertificadoEps() { return certificadoEps; }
    public void setCertificadoEps(String certificadoEps) { this.certificadoEps = certificadoEps; }

    // =========================
    // Helpers
    // =========================
    public String getNombreCompleto() {
        String n = (nombres == null) ? "" : nombres.trim();
        String a = (apellidos == null) ? "" : apellidos.trim();
        return (n + " " + a).trim();
    }

    // =========================
    // equals / hashCode / toString
    // =========================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nino)) return false;
        Nino nino = (Nino) o;
        // Si idNino == 0 (transiente), comparar por campos clave mínimos
        if (this.idNino == 0 || nino.idNino == 0) {
            return Objects.equals(documento, nino.documento)
                    && Objects.equals(nombres, nino.nombres)
                    && Objects.equals(apellidos, nino.apellidos)
                    && Objects.equals(fechaNacimiento, nino.fechaNacimiento);
        }
        return idNino == nino.idNino;
    }

    @Override
    public int hashCode() {
        return (idNino == 0)
                ? Objects.hash(documento, nombres, apellidos, fechaNacimiento)
                : Objects.hash(idNino);
    }

    @Override
    public String toString() {
        return "Nino{" +
                "idNino=" + idNino +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", documento=" + documento +
                ", genero='" + genero + '\'' +
                ", nacionalidad='" + nacionalidad + '\'' +
                ", fechaIngreso=" + fechaIngreso +
                ", hogarId=" + hogarId +
                ", padreId=" + padreId +
                ", foto='" + foto + '\'' +
                ", carnetVacunacion='" + carnetVacunacion + '\'' +
                ", certificadoEps='" + certificadoEps + '\'' +
                '}';
    }
}
