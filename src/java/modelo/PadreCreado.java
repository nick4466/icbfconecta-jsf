package modelo;

public class PadreCreado {
    private int idUsuario;
    private int idPadre;

    public PadreCreado(int idUsuario, int idPadre) {
        this.idUsuario = idUsuario;
        this.idPadre = idPadre;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdPadre() {
        return idPadre;
    }
}
