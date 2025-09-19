package control;

import dao.NinoDAO;
import dao.PadreDAO;
import dao.UsuarioDAO;
import modelo.Nino;
import modelo.Padre;
import modelo.Usuario;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@ManagedBean(name = "ninoBean")
@ViewScoped
public class NinoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Nino nino;
    private Padre padre;
    private Usuario usuarioPadre = new Usuario();

    // Campos de contraseña
    private String password1;
    private String password2;

    // Archivos
    private Part fotoNinoPart;
    private Part docPadrePart;

    // DAOs
    private NinoDAO ninoDAO;
    private PadreDAO padreDAO;
    private UsuarioDAO usuarioDAO;
    
    // Ruta base de uploads
    private static final String BASE_DIR = "C:/icbf_uploads";

    @PostConstruct
    public void init() {
        nino = new Nino();
        padre = new Padre();
        usuarioPadre = new Usuario();
        ninoDAO = new NinoDAO();
        padreDAO = new PadreDAO();
        usuarioDAO = new UsuarioDAO();
    }

   public void guardarMatricula() {
    FacesContext ctx = FacesContext.getCurrentInstance();
    try {
        // =============================
        // 1. Validar archivos
        // =============================
        if (fotoNinoPart == null || fotoNinoPart.getSize() == 0) {
            throw new RuntimeException("Debe adjuntar la foto del niño.");
        }
        if (docPadrePart == null || docPadrePart.getSize() == 0) {
            throw new RuntimeException("Debe adjuntar el documento del padre.");
        }

        // =============================
        // 2. Validar contraseñas
        // =============================
        if (password1 == null || password1.isEmpty() || password2 == null || password2.isEmpty()) {
            throw new RuntimeException("Debe ingresar y confirmar la contraseña del padre.");
        }
        if (!password1.equals(password2)) {
            throw new RuntimeException("Las contraseñas no coinciden.");
        }

        // =============================
        // 3. Crear o reutilizar USUARIO del padre
        // =============================
        Usuario usuarioExistente = usuarioDAO.findByDocumento(Long.parseLong(usuarioPadre.getDocumento()));

        int usuarioId;
        if (usuarioExistente != null) {
            usuarioId = usuarioExistente.getIdUsuario();
        } else {
            int rolPadreId = usuarioDAO.obtenerRolId("padre");
            usuarioPadre.setRolId(rolPadreId);
            usuarioPadre.setPasswordHash(sha256(password1)); // encriptamos en Java

            usuarioId = usuarioDAO.insertPadre(usuarioPadre);
        }

        // =============================
        // 4. Crear registro en PADRES
        // =============================
        padre.setUsuarioId(usuarioId);

        // guardamos archivo del documento de identidad
        String rutaDoc = guardarArchivo(docPadrePart, "padres/" + usuarioId);
        padre.setDocumentoIdentidadImg(rutaDoc);

        int idPadre = padreDAO.insert(padre);
        nino.setPadreId(idPadre);

        // =============================
        // 5. Crear NIÑO
        // =============================
        int idNino = ninoDAO.insert(nino);

        // guardamos foto y actualizamos
        String rutaFoto = guardarArchivo(fotoNinoPart, "ninos/" + idNino);
        ninoDAO.updateFoto(idNino, rutaFoto);

        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Matrícula guardada correctamente", null));

        limpiarFormulario();

    } catch (Exception e) {
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error al guardar: " + e.getMessage(), null));
        e.printStackTrace();
    }
}


    // =============================
    // Guardar archivo en carpeta
    // =============================
    private String guardarArchivo(Part part, String carpetaRelativa) throws Exception {
        String nombreOriginal = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String ext = nombreOriginal.contains(".") ? nombreOriginal.substring(nombreOriginal.lastIndexOf('.') + 1) : "";
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String nuevoNombre = uuid + "." + ext;

        Path destinoDir = Paths.get(BASE_DIR, carpetaRelativa);
        Files.createDirectories(destinoDir);

        Path destino = destinoDir.resolve(nuevoNombre);

        try (InputStream in = part.getInputStream()) {
            Files.copy(in, destino);
        }

        return carpetaRelativa + "/" + nuevoNombre; // ruta relativa para BD
    }

    // =============================
    // SHA-256 para password
    // =============================
    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo calcular SHA-256", e);
        }
    }

    public void cancelar() {
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        nino = new Nino();
        padre = new Padre();
        usuarioPadre = new Usuario();
        password1 = null;
        password2 = null;
        fotoNinoPart = null;
        docPadrePart = null;
    }
    // En la clase NinoBean

private List<Nino> listaNinos;

// getter y setter
public List<Nino> getListaNinos() {
    if (listaNinos == null) {
        cargarNinos();
    }
    return listaNinos;
}

public void setListaNinos(List<Nino> listaNinos) {
    this.listaNinos = listaNinos;
}

// Cargar niños por hogar
public void cargarNinos() {
    try {
        // Aquí debes pasar el id del hogar de la madre logueada
        int idHogar = 1; // ⚠️ por ahora fijo, luego lo obtendrás de SessionBean
        listaNinos = ninoDAO.listarNinosPorHogar(idHogar);
    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Error al cargar los niños: " + e.getMessage(), null));
        e.printStackTrace();
    }
}

// Eliminar niño
public void eliminar(int idNino) {
    try {
        boolean eliminado = ninoDAO.eliminarNino(idNino);
        if (eliminado) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Niño eliminado correctamente", null));
            cargarNinos(); // recargar lista
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN,
                "No se pudo eliminar el niño", null));
        }
    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Error al eliminar niño: " + e.getMessage(), null));
        e.printStackTrace();
    }
}


    // =============================
    // Getters y Setters
    // =============================
    public Nino getNino() { return nino; }
    public void setNino(Nino nino) { this.nino = nino; }

    public Padre getPadre() { return padre; }
    public void setPadre(Padre padre) { this.padre = padre; }

    public Usuario getUsuarioPadre() { return usuarioPadre; }
    public void setUsuarioPadre(Usuario usuarioPadre) { this.usuarioPadre = usuarioPadre; }

    public String getPassword1() { return password1; }
    public void setPassword1(String password1) { this.password1 = password1; }

    public String getPassword2() { return password2; }
    public void setPassword2(String password2) { this.password2 = password2; }

    public Part getFotoNinoPart() { return fotoNinoPart; }
    public void setFotoNinoPart(Part fotoNinoPart) { this.fotoNinoPart = fotoNinoPart; }

    public Part getDocPadrePart() { return docPadrePart; }
    public void setDocPadrePart(Part docPadrePart) { this.docPadrePart = docPadrePart; }
}
