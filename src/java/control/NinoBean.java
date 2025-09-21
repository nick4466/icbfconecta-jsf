package control;

import dao.HogarComunitarioDAO;
import dao.NinoDAO;
import dao.PadreDAO;
import dao.UsuarioDAO;
import modelo.Nino;
import modelo.Padre;
import modelo.Usuario;
import modelo.HogarComunitario;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

@ManagedBean(name = "ninoBean")
@ViewScoped
public class NinoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Nino nino;
    private Padre padre;
    private Usuario usuarioPadre;

    private String password1;
    private String password2;

    private Part fotoNinoPart;
    private Part docPadrePart;

    private NinoDAO ninoDAO;
    private PadreDAO padreDAO;
    private UsuarioDAO usuarioDAO;
    private HogarComunitarioDAO hogarDAO;

    private static final String BASE_DIR = "C:/icbf_uploads";

    private List<Nino> listaNinos;
    private List<HogarComunitario> listaHogares;

    @PostConstruct
    public void init() {
        nino = new Nino();
        padre = new Padre();
        usuarioPadre = new Usuario();
        ninoDAO = new NinoDAO();
        padreDAO = new PadreDAO();
        usuarioDAO = new UsuarioDAO();
        hogarDAO = new HogarComunitarioDAO();

        // cargar hogares activos para combo dinámico
        listaHogares = hogarDAO.listarActivos();
    }

    public String guardarMatricula() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            // =============================
            // 1. Validar archivos obligatorios
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
            if (password1 == null || password2 == null || !password1.equals(password2)) {
                throw new RuntimeException("Contraseñas inválidas o no coinciden.");
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
                usuarioPadre.setPasswordHash(sha256(password1));
                usuarioId = usuarioDAO.insertPadre(usuarioPadre);
            }

            // =============================
            // 4. Crear o reutilizar PADRE
            // =============================
            Padre padreExistente = padreDAO.findByUsuarioId(usuarioId);
            int idPadre;
            if (padreExistente != null) {
                idPadre = padreExistente.getIdPadre();
                String rutaDoc = guardarArchivo(docPadrePart, "padres/" + usuarioId);
                padreDAO.updateDocumento(idPadre, rutaDoc);
            } else {
                padre.setUsuarioId(usuarioId);
                String rutaDoc = guardarArchivo(docPadrePart, "padres/" + usuarioId);
                padre.setDocumentoIdentidadImg(rutaDoc);
                idPadre = padreDAO.insert(padre);
            }

            // =============================
            // 5. Crear NIÑO
            // =============================
            nino.setPadreId(usuarioId); // FK al usuario del padre
            int idNino = ninoDAO.insert(nino);

            String rutaFoto = guardarArchivo(fotoNinoPart, "ninos/" + idNino);
            ninoDAO.updateFoto(idNino, rutaFoto);

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Matrícula guardada correctamente", null));

            limpiarFormulario();
            cargarNinos();

        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al guardar: " + e.getMessage(), null));
            e.printStackTrace();
        }
        return "listarNinos?faces-redirect=true";

    }

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

    public void cargarNinos() {
        try {
            int idHogar = 1; // ⚠️ reemplazar luego por el hogar real de la madre logueada
            listaNinos = ninoDAO.listarNinosPorHogar(idHogar);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error al cargar los niños: " + e.getMessage(), null));
        }
    }

    public void eliminar(int idNino) {
        try {
            boolean eliminado = ninoDAO.eliminarNino(idNino);
            if (eliminado) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Niño eliminado correctamente", null));
                cargarNinos();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "No se pudo eliminar el niño", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error al eliminar niño: " + e.getMessage(), null));
        }
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
    
    public String cancelar() {
    // Redirige a la página de listado de niños
    return "listarNinos?faces-redirect=true";
}
    public void cargarNinoPorId() {
        try {
            if (FacesContext.getCurrentInstance().isPostback()) {
                return; // evitar sobreescribir los datos enviados por el usuario en POST
            }

            if (nino != null && nino.getIdNino() > 0) {
                Nino cargado = ninoDAO.buscarNinoPorId(nino.getIdNino());
                if (cargado != null) {
                    this.nino = cargado; // reemplazamos el niño con los datos de BD
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "No se encontró el niño con ID: " + nino.getIdNino(), null));
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error al cargar el niño: " + e.getMessage(), null));
            e.printStackTrace();
        }
    }


  public String actualizarNino() {
    try {
        if (fotoNinoPart != null && fotoNinoPart.getSize() > 0) {
            String rutaFoto = guardarArchivo(fotoNinoPart, "ninos/" + nino.getIdNino());
            nino.setFoto(rutaFoto);
        }

        boolean actualizado = ninoDAO.actualizarNino(nino);
        if (actualizado) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Niño actualizado correctamente", null));
            return "listarNinos?faces-redirect=true"; // ✅ vuelve al listado
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN,
                "No se pudo actualizar el niño", null));
            return null; // ❌ se queda en la misma página
        }
    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Error al actualizar niño: " + e.getMessage(), null));
        return null;
    }
}







    // Getters y Setters
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

    public List<Nino> getListaNinos() {
        if (listaNinos == null) {
            cargarNinos();
        }
        return listaNinos;
    }
    public void setListaNinos(List<Nino> listaNinos) { this.listaNinos = listaNinos; }

    public List<HogarComunitario> getListaHogares() { return listaHogares; }
}
