package modelo;

public class HogarComunitario {
    private int idHogar;
    private String nombreHogar;
    private String direccion;
    private String localidad;
    private int capacidadMaxima;
    private String estado;
    private int madreId;

    // Getters y setters
    public int getIdHogar() { return idHogar; }
    public void setIdHogar(int idHogar) { this.idHogar = idHogar; }

    public String getNombreHogar() { return nombreHogar; }
    public void setNombreHogar(String nombreHogar) { this.nombreHogar = nombreHogar; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public int getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getMadreId() { return madreId; }
    public void setMadreId(int madreId) { this.madreId = madreId; }
}
