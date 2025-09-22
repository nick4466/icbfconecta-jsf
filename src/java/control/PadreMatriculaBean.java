package control;

import dao.PadreDAO;
import dao.UsuarioDAO;
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
import java.util.UUID;

@ManagedBean(name = "padreMatriculaBean")
@ViewScoped
public class PadreMatriculaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String BASE_DIR = "C:/icbf_uploads";

    private Padre padre;
    private Usuario usuarioPadre;
    private String password1;
    private String password2;
    private Part documentoPart;

    private transient PadreDAO padreDAO;
    private transient UsuarioDAO usuarioDAO;

    @PostConstruct
    public void init() {
        padre = new Padre();
        usuarioPadre = new Usuario();
        padreDAO = new PadreDAO();
        usuarioDAO = new UsuarioDAO();
    }

    public String guardarPadre() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            validarFormulario();

            long documento = Long.parseLong(usuarioPadre.getDocumento().trim());
            usuarioPadre.setDocumento(String.valueOf(documento));
            usuarioPadre.setNombres(usuarioPadre.getNombres().trim());
            usuarioPadre.setApellidos(usuarioPadre.getApellidos().trim());
            usuarioPadre.setCorreo(usuarioPadre.getCorreo().trim());
            if (usuarioPadre.getDireccion() != null) {
                usuarioPadre.setDireccion(usuarioPadre.getDireccion().trim());
            }
            if (usuarioPadre.getTelefono() != null) {
                usuarioPadre.setTelefono(usuarioPadre.getTelefono().trim());
            }

            String passwordHash = sha256(password1);

            Usuario existente = usuarioDAO.findByDocumento(documento);
            int usuarioId;

            if (existente == null) {
                usuarioPadre.setPasswordHash(passwordHash);
                usuarioId = usuarioDAO.insertPadre(usuarioPadre);
            } else {
                usuarioPadre.setIdUsuario(existente.getIdUsuario());
                usuarioDAO.actualizarDatosPadre(usuarioPadre, passwordHash);
                usuarioId = existente.getIdUsuario();
            }

            String rutaDocumento = guardarArchivo(documentoPart, "padres/" + usuarioId);

            Padre padreExistente = padreDAO.findByUsuarioId(usuarioId);
            int idPadre;
            if (padre.getOcupacion() != null) {
                padre.setOcupacion(padre.getOcupacion().trim());
            }
            if (padre.getTelefonoContactoEmergencia() != null) {
                padre.setTelefonoContactoEmergencia(padre.getTelefonoContactoEmergencia().trim());
            }
            if (padre.getNombreContactoEmergencia() != null) {
                padre.setNombreContactoEmergencia(padre.getNombreContactoEmergencia().trim());
            }
            if (padre.getSituacionEconomicaHogar() != null) {
                padre.setSituacionEconomicaHogar(padre.getSituacionEconomicaHogar().trim());
            }
            if (padreExistente == null) {
                padre.setUsuarioId(usuarioId);
                padre.setDocumentoIdentidadImg(rutaDocumento);
                idPadre = padreDAO.insert(padre);
            } else {
                padre.setIdPadre(padreExistente.getIdPadre());
                padre.setUsuarioId(usuarioId);
                padre.setDocumentoIdentidadImg(rutaDocumento);
                padreDAO.actualizarPadre(padre);
                idPadre = padreExistente.getIdPadre();
            }

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Datos del padre guardados correctamente. Continúa con el registro del niño.", null));

            limpiarFormulario();
            return "crearNino?faces-redirect=true&padreId=" + idPadre;
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al guardar los datos del padre: " + e.getMessage(), null));
            return null;
        }
    }

    private void validarFormulario() {
        if (usuarioPadre.getDocumento() == null || usuarioPadre.getDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("El documento del padre es obligatorio.");
        }
        try {
            Long.parseLong(usuarioPadre.getDocumento().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El documento del padre debe ser numérico.");
        }
        if (usuarioPadre.getNombres() == null || usuarioPadre.getNombres().trim().isEmpty()) {
            throw new IllegalArgumentException("Los nombres del padre son obligatorios.");
        }
        if (usuarioPadre.getApellidos() == null || usuarioPadre.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos del padre son obligatorios.");
        }
        if (usuarioPadre.getCorreo() == null || usuarioPadre.getCorreo().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (password1 == null || password2 == null || !password1.equals(password2)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }
        if (documentoPart == null || documentoPart.getSize() == 0) {
            throw new IllegalArgumentException("Debes adjuntar la imagen del documento de identidad.");
        }
    }

    private String guardarArchivo(Part part, String carpetaRelativa) throws Exception {
        String nombreOriginal = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String extension = "";
        int indice = nombreOriginal.lastIndexOf('.');
        if (indice != -1) {
            extension = nombreOriginal.substring(indice + 1);
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String nuevoNombre = uuid + (extension.isEmpty() ? "" : "." + extension);

        Path destinoDir = Paths.get(BASE_DIR, carpetaRelativa);
        Files.createDirectories(destinoDir);

        Path destino = destinoDir.resolve(nuevoNombre);
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, destino);
        }
        return carpetaRelativa + "/" + nuevoNombre;
    }

    private String sha256(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void limpiarFormulario() {
        padre = new Padre();
        usuarioPadre = new Usuario();
        password1 = null;
        password2 = null;
        documentoPart = null;
    }

    public Padre getPadre() {
        return padre;
    }

    public void setPadre(Padre padre) {
        this.padre = padre;
    }

    public Usuario getUsuarioPadre() {
        return usuarioPadre;
    }

    public void setUsuarioPadre(Usuario usuarioPadre) {
        this.usuarioPadre = usuarioPadre;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public Part getDocumentoPart() {
        return documentoPart;
    }

    public void setDocumentoPart(Part documentoPart) {
        this.documentoPart = documentoPart;
    }
}
