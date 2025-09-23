package control;


import dao.AsistenciaDAO;
import dao.HogarComunitarioDAO;
import dao.NinoDAO;
import dao.UsuarioDAO;
import modelo.Asistencia;
import modelo.HogarComunitario;
import modelo.Nino;
import modelo.Usuario;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

// AsistenciaBean.java
@ManagedBean
@SessionScoped
public class AsistenciaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final NinoDAO ninoDAO = new NinoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final HogarComunitarioDAO hogarDAO = new HogarComunitarioDAO();
    private transient DataFormatter dataFormatter = new DataFormatter();

    private Asistencia asistencia = new Asistencia();
    private Part archivoExcel;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    // Lista de asistencias
    public List<Asistencia> getLista() {
        Usuario usuario = obtenerUsuarioSesion();
        if (usuario != null && "madre_comunitaria".equals(usuario.getRol())) {
            return asistenciaDAO.listarPorMadre(usuario.getIdUsuario());
        }
        return asistenciaDAO.listarConNombres();
    }

    // Lista de niños para el combo
    public List<Nino> getNinos() {
        Usuario usuario = obtenerUsuarioSesion();
        if (usuario != null && "madre_comunitaria".equals(usuario.getRol())) {
            return ninoDAO.listarPorMadre(usuario.getIdUsuario());
        }
        return ninoDAO.listar();
    }

    // Guardar
    public String guardar() {
        asistencia.setFecha(new Date(System.currentTimeMillis()));
        asistenciaDAO.insertar(asistencia);
        asistencia = new Asistencia(); // limpiar
        return "listarAsistencia?faces-redirect=true";
    }

    // ==========================
    // Carga masiva desde Excel
    // ==========================
    public void cargarDesdeExcel() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (archivoExcel == null || archivoExcel.getSize() == 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Debes seleccionar un archivo Excel (.xls o .xlsx).", null));
            return;
        }

        Usuario usuarioSesion = obtenerUsuarioSesion();
        boolean esMadreSesion = usuarioSesion != null && "madre_comunitaria".equals(usuarioSesion.getRol());

        int procesadas = 0;
        int exitosas = 0;
        List<String> errores = new ArrayList<>();

        try (InputStream in = archivoExcel.getInputStream();
             Workbook workbook = WorkbookFactory.create(in)) {

            if (workbook.getNumberOfSheets() == 0) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "El archivo no contiene hojas para procesar.", null));
                return;
            }

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Se omite la fila de encabezados
                Row row = sheet.getRow(i);
                if (row == null || esFilaVacia(row)) {
                    continue;
                }

                procesadas++;
                try {
                    String documentoMadreStr = obtenerString(row.getCell(0));
                    String documentoNinoStr = obtenerString(row.getCell(1));
                    Date fecha = obtenerFecha(row.getCell(2));
                    String estado = normalizarEstado(obtenerString(row.getCell(3)));

                    if (documentoNinoStr == null || documentoNinoStr.isEmpty()) {
                        throw new IllegalArgumentException("El documento del niño es obligatorio.");
                    }

                    Usuario madre;
                    if (esMadreSesion) {
                        madre = usuarioSesion;
                        String documentoSesion = madre.getDocumento() != null ? madre.getDocumento().trim() : "";
                        if (documentoMadreStr != null && !documentoMadreStr.isEmpty()
                                && !documentoSesion.equals(documentoMadreStr.trim())) {
                            throw new IllegalArgumentException("El documento de la madre en el archivo no coincide con el usuario logueado.");
                        }
                    } else {
                        if (documentoMadreStr == null || documentoMadreStr.isEmpty()) {
                            throw new IllegalArgumentException("El documento de la madre es obligatorio.");
                        }
                        madre = buscarMadrePorDocumento(documentoMadreStr);
                        if (madre == null) {
                            throw new IllegalArgumentException("No se encontró una madre con documento " + documentoMadreStr + ".");
                        }
                    }

                    HogarComunitario hogar = hogarDAO.buscarPorMadre(madre.getIdUsuario());
                    if (hogar == null) {
                        throw new IllegalArgumentException("La madre indicada no tiene un hogar asignado.");
                    }

                    Nino nino = buscarNinoPorDocumento(documentoNinoStr);
                    if (nino == null) {
                        throw new IllegalArgumentException("No se encontró un niño con documento " + documentoNinoStr + ".");
                    }

                    if (nino.getHogarId() != hogar.getIdHogar()) {
                        throw new IllegalArgumentException("El niño no pertenece al hogar de la madre indicada.");
                    }

                    Asistencia registro = new Asistencia();
                    registro.setIdNino(nino.getIdNino());
                    registro.setFecha(fecha);
                    registro.setEstado(estado);

                    if (asistenciaDAO.insertar(registro)) {
                        exitosas++;
                    } else {
                        throw new IllegalStateException("No se pudo guardar la asistencia del niño " + documentoNinoStr + ".");
                    }
                } catch (Exception e) {
                    errores.add("Fila " + (i + 1) + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al procesar el archivo: " + e.getMessage(), null));
            return;
        } finally {
            archivoExcel = null;
        }

        if (procesadas == 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "No se encontraron registros para importar.", null));
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Importación finalizada: " + exitosas + " de " + procesadas + " registros importados.", null));
        }

        for (String error : errores) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, error, null));
        }
    }

    private Usuario obtenerUsuarioSesion() {
        return sessionBean != null ? sessionBean.getUsuarioLogueado() : null;
    }

    private Usuario buscarMadrePorDocumento(String documento) {
        long documentoMadre = parsearDocumento(documento, "de la madre");
        return usuarioDAO.findByDocumento(documentoMadre);
    }

    private Nino buscarNinoPorDocumento(String documento) {
        long documentoNino = parsearDocumento(documento, "del niño");
        return ninoDAO.buscarPorDocumento(documentoNino);
    }

    private long parsearDocumento(String valor, String etiqueta) {
        try {
            return Long.parseLong(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El documento " + etiqueta + " debe ser numérico (valor recibido: '" + valor + "').");
        }
    }

    private Date obtenerFecha(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return new Date(cell.getDateCellValue().getTime());
        }

        String valor = obtenerString(cell);
        if (valor == null || valor.isEmpty()) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }

        try {
            LocalDate localDate = LocalDate.parse(valor.trim());
            return Date.valueOf(localDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido ('" + valor + "'). Usa AAAA-MM-DD.");
        }
    }

    private String normalizarEstado(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }

        String estado = valor.trim().toLowerCase();
        switch (estado) {
            case "presente":
            case "p":
                return "Presente";
            case "ausente":
            case "a":
                return "Ausente";
            case "justificado":
            case "j":
                return "Justificado";
            default:
                throw new IllegalArgumentException("Estado inválido ('" + valor + "'). Usa Presente, Ausente o Justificado.");
        }
    }

    private String obtenerString(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (dataFormatter == null) {
            dataFormatter = new DataFormatter();
        }
        String valor = dataFormatter.formatCellValue(cell);
        return valor != null ? valor.trim() : null;
    }

    private boolean esFilaVacia(Row row) {
        if (row == null) {
            return true;
        }
        for (int c = 0; c <= 3; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String valor = obtenerString(cell);
                if (valor != null && !valor.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    // ==========================
    // Editar
    // ==========================
    public String editar(Asistencia a) {
        this.asistencia = a; // cargamos la asistencia seleccionada
        return "editarAsistencia?faces-redirect=true";
    }

    public String actualizar() {
        asistenciaDAO.actualizar(asistencia);
        asistencia = new Asistencia(); // limpiar
        return "listarAsistencia?faces-redirect=true";
    }

    // ==========================
    // Eliminar
    // ==========================
    public String eliminar(int idAsistencia) {
        asistenciaDAO.eliminar(idAsistencia);
        return "listarAsistencia?faces-redirect=true";
    }

    // Getters y setters
    public Asistencia getAsistencia() { return asistencia; }
    public void setAsistencia(Asistencia asistencia) { this.asistencia = asistencia; }

    public Part getArchivoExcel() { return archivoExcel; }
    public void setArchivoExcel(Part archivoExcel) { this.archivoExcel = archivoExcel; }

    public SessionBean getSessionBean() { return sessionBean; }
    public void setSessionBean(SessionBean sessionBean) { this.sessionBean = sessionBean; }
}
