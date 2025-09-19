package modelo;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private String documento;
    private String nombres;
    private String apellidos;
    private String correo;
    private String direccion;
    private String telefono;
    private String rol;

    // ⚡ nuevo campo para coincidir con la BD
    private String passwordHash;

    // ⚡ también necesitamos rolId para los insert
    private int rolId;


    // Getters y setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    // ⚡ nuevo getter/setter de passwordHash
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    // ⚡ getter/setter de rolId (FK hacia roles)
    public int getRolId() { return rolId; }
    public void setRolId(int rolId) { this.rolId = rolId; }
}
