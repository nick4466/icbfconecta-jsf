package control;

import dao.HogarComunitarioDAO;
import dao.NinoDAO;
import dao.PadreDAO;
import dao.UsuarioDAO;
import modelo.HogarComunitario;
import modelo.Nino;
import modelo.Padre;
import modelo.Usuario;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ManagedBean(name = "ninoBean")
@ViewScoped
public class NinoBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String BASE_DIR = "C:/icbf_uploads";

    private Nino nino;
    private Part fotoNinoPart;

    private Integer padreIdSeleccionado;
    private Padre padreSeleccionado;
    private Usuario usuarioPadreSeleccionado;

    private transient NinoDAO ninoDAO;
    private transient PadreDAO padreDAO;
    private transient UsuarioDAO usuarioDAO;
    private transient HogarComunitarioDAO hogarDAO;

    private List<Nino> listaNinos;
    private List<HogarComunitario> listaHogares;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @PostConstruct
    public void init() {
        nino = new Nino();
        ninoDAO = new NinoDAO();
        padreDAO = new PadreDAO();
        usuarioDAO = new UsuarioDAO();
        hogarDAO = new HogarComunitarioDAO();
        cargarHogaresDisponibles();
        cargarPadreDesdeParametros();
    }

    private void cargarHogaresDisponibles() {
        listaHogares = new ArrayList<>();
        HogarComunitarioDAO dao = getHogarDAO();
        if (sessionBean != null && sessionBean.getUsuarioLogueado() != null) {
            Usuario usuario = sessionBean.getUsuarioLogueado();
            if ("madre_comunitaria".equals(usuario.getRol())) {
                HogarComunitario hogar = dao.buscarPorMadre(usuario.getIdUsuario());
                if (hogar != null) {
                    listaHogares.add(hogar);
                }
                return;
            }
        }
        listaHogares = dao.listarActivos();
    }

    private NinoDAO getNinoDAO() {
        if (ninoDAO == null) {
            ninoDAO = new NinoDAO();
        }
        return ninoDAO;
    }

    private PadreDAO getPadreDAO() {
        if (padreDAO == null) {
            padreDAO = new PadreDAO();
        }
        return padreDAO;
    }

    private UsuarioDAO getUsuarioDAO() {
        if (usuarioDAO == null) {
            usuarioDAO = new UsuarioDAO();
        }
        return usuarioDAO;
    }

    private HogarComunitarioDAO getHogarDAO() {
        if (hogarDAO == null) {
            hogarDAO = new HogarComunitarioDAO();
        }
        return hogarDAO;
    }

    private void limpiarPadreSeleccionado() {
        padreIdSeleccionado = null;
        padreSeleccionado = null;
        usuarioPadreSeleccionado = null;
        if (nino != null) {
            nino.setPadreId(0);
        }
    }

    private void sincronizarPadreSeleccionado(Integer idPadre) {
        if (idPadre == null || idPadre <= 0) {
            limpiarPadreSeleccionado();
            return;
        }

        if (padreIdSeleccionado != null && padreSeleccionado != null
                && padreSeleccionado.getIdPadre() != null
                && padreSeleccionado.getIdPadre().equals(idPadre)) {
            Integer usuarioId = padreSeleccionado.getUsuarioId();
            boolean usuarioSincronizado = (usuarioId == null && usuarioPadreSeleccionado == null)
                    || (usuarioId != null && usuarioPadreSeleccionado != null
                        && usuarioPadreSeleccionado.getIdUsuario() == usuarioId);
            if (usuarioSincronizado) {
                if (nino != null) {
                    nino.setPadreId(idPadre);
                }
                return;
            }
        }

        padreIdSeleccionado = idPadre;
        padreSeleccionado = getPadreDAO().findById(idPadre);
        if (padreSeleccionado == null) {
            limpiarPadreSeleccionado();
            return;
        }

        if (padreSeleccionado.getUsuarioId() != null) {
            usuarioPadreSeleccionado = getUsuarioDAO().findById(padreSeleccionado.getUsuarioId());
        } else {
            usuarioPadreSeleccionado = null;
        }

        if (nino != null) {
            nino.setPadreId(idPadre);
        }
    }

    private void cargarPadreDesdeParametros() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null) {
            return;
        }
        ExternalContext external = ctx.getExternalContext();
        if (external == null) {
            return;
        }
        Map<String, String> params = external.getRequestParameterMap();
        if (params == null) {
            return;
        }
        String padreParam = params.get("padreId");
        if (padreParam == null || padreParam.trim().isEmpty()) {
            return;
        }
        try {
            sincronizarPadreSeleccionado(Integer.valueOf(padreParam.trim()));
        } catch (NumberFormatException e) {
            limpiarPadreSeleccionado();
        }
    }

    public String guardarMatricula() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            if (padreIdSeleccionado == null || padreIdSeleccionado <= 0) {
                throw new RuntimeException("Debes registrar primero los datos del padre.");
            }
            if (fotoNinoPart == null || fotoNinoPart.getSize() == 0) {
                throw new RuntimeException("Debes adjuntar la foto del niño.");
            }

            nino.setPadreId(padreIdSeleccionado);
            int idNino = getNinoDAO().insert(nino);

            String rutaFoto = guardarArchivo(fotoNinoPart, "ninos/" + idNino);
            getNinoDAO().updateFoto(idNino, rutaFoto);

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Matrícula guardada correctamente", null));

            limpiarFormulario();
            cargarNinos();
            return "listarNinos?faces-redirect=true";
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al guardar: " + e.getMessage(), null));
            return null;
        }
    }

    private String guardarArchivo(Part part, String carpetaRelativa) throws Exception {
        String nombreOriginal = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String extension = "";
        int idx = nombreOriginal.lastIndexOf('.');
        if (idx != -1 && idx < nombreOriginal.length() - 1) {
            extension = nombreOriginal.substring(idx + 1);
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String nuevoNombre = extension.isEmpty() ? uuid : uuid + "." + extension;

        Path destinoDir = Paths.get(BASE_DIR, carpetaRelativa);
        Files.createDirectories(destinoDir);

        Path destino = destinoDir.resolve(nuevoNombre);
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, destino);
        }

        return carpetaRelativa + "/" + nuevoNombre;
    }

    public void cargarNinos() {
        try {
            if (sessionBean != null && sessionBean.getUsuarioLogueado() != null) {
                Usuario usuario = sessionBean.getUsuarioLogueado();
                if ("madre_comunitaria".equals(usuario.getRol())) {
                    listaNinos = getNinoDAO().listarPorMadre(usuario.getIdUsuario());
                    return;
                }
            }
            listaNinos = getNinoDAO().listar();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error al cargar los niños: " + e.getMessage(), null));
        }
    }

    public void eliminar(int idNino) {
        try {
            boolean eliminado = getNinoDAO().eliminarNino(idNino);
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
        if (padreIdSeleccionado != null) {
            nino.setPadreId(padreIdSeleccionado);
        }
        fotoNinoPart = null;
    }

    public String cancelar() {
        return "listarNinos?faces-redirect=true";
    }

    public void cargarNinoPorId() {
        try {
            if (FacesContext.getCurrentInstance().isPostback()) {
                return;
            }

            if (nino != null && nino.getIdNino() > 0) {
                Nino cargado = getNinoDAO().buscarNinoPorId(nino.getIdNino());
                if (cargado != null) {
                    this.nino = cargado;
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

            boolean actualizado = getNinoDAO().actualizarNino(nino);
            if (actualizado) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Niño actualizado correctamente", null));
                return "listarNinos?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "No se pudo actualizar el niño", null));
                return null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error al actualizar niño: " + e.getMessage(), null));
            return null;
        }
    }

    public Nino getNino() {
        return nino;
    }

    public void setNino(Nino nino) {
        this.nino = nino;
    }

    public Part getFotoNinoPart() {
        return fotoNinoPart;
    }

    public void setFotoNinoPart(Part fotoNinoPart) {
        this.fotoNinoPart = fotoNinoPart;
    }

    public Integer getPadreIdSeleccionado() {
        return padreIdSeleccionado;
    }

    public void setPadreIdSeleccionado(Integer padreIdSeleccionado) {
        sincronizarPadreSeleccionado(padreIdSeleccionado);
    }

    public Long getPadreIdSeleccionadoHidden() {
        return padreIdSeleccionado != null ? padreIdSeleccionado.longValue() : null;
    }

    public void setPadreIdSeleccionadoHidden(Long valor) {
        if (valor == null) {
            limpiarPadreSeleccionado();
        } else {
            sincronizarPadreSeleccionado(valor.intValue());
        }
    }

    public Padre getPadreSeleccionado() {
        return padreSeleccionado;
    }

    public Usuario getUsuarioPadreSeleccionado() {
        return usuarioPadreSeleccionado;
    }

    public boolean isPadreDisponible() {
        return padreIdSeleccionado != null
                && padreSeleccionado != null
                && usuarioPadreSeleccionado != null;
    }

    public List<Nino> getListaNinos() {
        if (listaNinos == null) {
            cargarNinos();
        }
        return listaNinos;
    }

    public void setListaNinos(List<Nino> listaNinos) {
        this.listaNinos = listaNinos;
    }

    public List<HogarComunitario> getListaHogares() {
        return listaHogares;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
        if (hogarDAO != null) {
            cargarHogaresDisponibles();
        }
    }
}
