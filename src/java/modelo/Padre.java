package modelo;

import java.io.Serializable;

public class Padre implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idPadre;                       // PK de la tabla padres
    private String ocupacion;                      // ocupacion
    private Integer estrato;                       // estrato
    private String telefonoContactoEmergencia;     // telefono_contacto_emergencia
    private String nombreContactoEmergencia;       // nombre_contacto_emergencia
    private String situacionEconomicaHogar;        // situacion_economica_hogar
    private String documentoIdentidadImg;          // documento_identidad_img (ruta del archivo)

    private Integer usuarioId;                     // FK hacia usuarios.id_usuario

    // =======================
    // Constructores
    // =======================
    public Padre() {}

    public Padre(Integer idPadre, String ocupacion, Integer estrato,
                 String telefonoContactoEmergencia, String nombreContactoEmergencia,
                 String situacionEconomicaHogar, String documentoIdentidadImg, Integer usuarioId) {
        this.idPadre = idPadre;
        this.ocupacion = ocupacion;
        this.estrato = estrato;
        this.telefonoContactoEmergencia = telefonoContactoEmergencia;
        this.nombreContactoEmergencia = nombreContactoEmergencia;
        this.situacionEconomicaHogar = situacionEconomicaHogar;
        this.documentoIdentidadImg = documentoIdentidadImg;
        this.usuarioId = usuarioId;
    }

    // =======================
    // Getters y Setters
    // =======================
    public Integer getIdPadre() {
        return idPadre;
    }

    public void setIdPadre(Integer idPadre) {
        this.idPadre = idPadre;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public Integer getEstrato() {
        return estrato;
    }

    public void setEstrato(Integer estrato) {
        this.estrato = estrato;
    }

    public String getTelefonoContactoEmergencia() {
        return telefonoContactoEmergencia;
    }

    public void setTelefonoContactoEmergencia(String telefonoContactoEmergencia) {
        this.telefonoContactoEmergencia = telefonoContactoEmergencia;
    }

    public String getNombreContactoEmergencia() {
        return nombreContactoEmergencia;
    }

    public void setNombreContactoEmergencia(String nombreContactoEmergencia) {
        this.nombreContactoEmergencia = nombreContactoEmergencia;
    }

    public String getSituacionEconomicaHogar() {
        return situacionEconomicaHogar;
    }

    public void setSituacionEconomicaHogar(String situacionEconomicaHogar) {
        this.situacionEconomicaHogar = situacionEconomicaHogar;
    }

    public String getDocumentoIdentidadImg() {
        return documentoIdentidadImg;
    }

    public void setDocumentoIdentidadImg(String documentoIdentidadImg) {
        this.documentoIdentidadImg = documentoIdentidadImg;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
}
