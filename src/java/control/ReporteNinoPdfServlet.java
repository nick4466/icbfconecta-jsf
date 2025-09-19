package control;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import dao.ReporteDAO;
import modelo.ReporteNinoDTO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import modelo.ReporteNinoDTO;

public class ReporteNinoPdfServlet extends HttpServlet {

    // Ajusta esta ruta si tu carpeta cambia.
    // Usa / en vez de \ para evitar escapes. En Windows funciona igual.
    private static final String BASE_UPLOAD_DIR = "C:/icbf_uploads";

    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        if (isBlank(idParam)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro 'id'.");
            return;
        }

        int idNino;
        try {
            idNino = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El parámetro 'id' no es válido.");
            return;
        }

        // Consulta del DTO (usa la VIEW vw_reporte_nino)
        ReporteDAO dao = new ReporteDAO();
        ReporteNinoDTO dto = dao.findById(idNino);
        if (dto == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No existe el reporte para el id_nino=" + idNino);
            return;
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=reporte_nino_" + idNino + ".pdf");

        Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
        try {
            PdfWriter.getInstance(doc, resp.getOutputStream());
            doc.addAuthor("ICBF Conecta");
            doc.addTitle("Reporte de Matrícula - Niño #" + idNino);
            doc.addSubject("Ficha del niño y datos del padre");
            doc.addCreationDate();

            doc.open();

            // ===== Encabezado =====
            Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, "Cp1252", true, 16);
            Font fSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, "Cp1252", true, 13);
            Font fLabel   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, "Cp1252", true, 10);
            Font fValor   = FontFactory.getFont(FontFactory.HELVETICA, "Cp1252", true, 10);

            Paragraph titulo = new Paragraph("REPORTE DE MATRÍCULA", fTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(12f);
            doc.add(titulo);

            Paragraph sub = new Paragraph("Generado: " + DF.format(new Date()), fValor);
            sub.setAlignment(Element.ALIGN_RIGHT);
            sub.setSpacingAfter(10f);
            doc.add(sub);

            // ===== Sección: Niño =====
            doc.add(new Paragraph("Datos del Niño", fSeccion));
            doc.add(espacio(6f));

            PdfPTable tNino = new PdfPTable(new float[]{30, 70});
            tNino.setWidthPercentage(100);

            tNino.addCell(cellLabel("Nombres:", fLabel));
            tNino.addCell(cellValor(dto.getNinoNombres() + " " + nullSafe(dto.getNinoApellidos()), fValor));

            tNino.addCell(cellLabel("Documento:", fLabel));
            tNino.addCell(cellValor(dto.getNinoDocumento() == null ? "-" : String.valueOf(dto.getNinoDocumento()), fValor));

            tNino.addCell(cellLabel("Fecha Nacimiento:", fLabel));
            tNino.addCell(cellValor(fmt(dto.getFechaNacimiento()), fValor));

            tNino.addCell(cellLabel("Género:", fLabel));
            tNino.addCell(cellValor(nullSafe(dto.getGenero()), fValor));

            tNino.addCell(cellLabel("Nacionalidad:", fLabel));
            tNino.addCell(cellValor(nullSafe(dto.getNacionalidad()), fValor));

            tNino.addCell(cellLabel("Fecha Ingreso:", fLabel));
            tNino.addCell(cellValor(fmt(dto.getFechaIngreso()), fValor));

            doc.add(tNino);
            doc.add(espacio(8f));

            // Imágenes del niño (si existen)
            PdfPTable tImgs = new PdfPTable(3);
            tImgs.setWidthPercentage(100);
            tImgs.setWidths(new float[]{33, 33, 34});

            addImageCell(tImgs, dto.getFoto(), "Foto");
            addImageCell(tImgs, dto.getCarnetVacunacion(), "Carnet de vacunación");
            addImageCell(tImgs, dto.getCertificadoEps(), "Certificado EPS");

            doc.add(tImgs);
            doc.add(espacio(12f));

            // ===== Sección: Padre =====
            doc.add(new Paragraph("Datos del Padre / Acudiente", fSeccion));
            doc.add(espacio(6f));

            PdfPTable tPadre = new PdfPTable(new float[]{30, 70});
            tPadre.setWidthPercentage(100);

            tPadre.addCell(cellLabel("Nombres:", fLabel));
            tPadre.addCell(cellValor(dto.getPadreNombres() + " " + nullSafe(dto.getPadreApellidos()), fValor));

            tPadre.addCell(cellLabel("Documento:", fLabel));
            tPadre.addCell(cellValor(nullSafe(dto.getPadreDocumento()), fValor));

            tPadre.addCell(cellLabel("Correo:", fLabel));
            tPadre.addCell(cellValor(nullSafe(dto.getPadreCorreo()), fValor));

            tPadre.addCell(cellLabel("Teléfono:", fLabel));
            tPadre.addCell(cellValor(nullSafe(dto.getPadreTelefono()), fValor));

            tPadre.addCell(cellLabel("Dirección:", fLabel));
            tPadre.addCell(cellValor(nullSafe(dto.getPadreDireccion()), fValor));

            tPadre.addCell(cellLabel("Ocupación:", fLabel));
            tPadre.addCell(cellValor(nullSafe(dto.getOcupacion()), fValor));

            tPadre.addCell(cellLabel("Estrato:", fLabel));
            tPadre.addCell(cellValor(dto.getEstrato() == null ? "-" : String.valueOf(dto.getEstrato()), fValor));

            tPadre.addCell(cellLabel("Contacto Emergencia:", fLabel));
            tPadre.addCell(cellValor(nullSafe(dto.getNomEmerg()) + " / " + nullSafe(dto.getTelEmerg()), fValor));

            tPadre.addCell(cellLabel("Situación Económica:", fLabel));
            tPadre.addCell(cellValor(nullSafe(dto.getSituacionEcon()), fValor));

            doc.add(tPadre);
            doc.add(espacio(12f));

            // ===== Sección: Hogar =====
            doc.add(new Paragraph("Hogar Comunitario", fSeccion));
            doc.add(espacio(6f));

            PdfPTable tHogar = new PdfPTable(new float[]{30, 70});
            tHogar.setWidthPercentage(100);

            tHogar.addCell(cellLabel("Nombre hogar:", fLabel));
            tHogar.addCell(cellValor(nullSafe(dto.getNombreHogar()), fValor));

            tHogar.addCell(cellLabel("Dirección:", fLabel));
            tHogar.addCell(cellValor(nullSafe(dto.getHogarDireccion()), fValor));

            tHogar.addCell(cellLabel("Localidad:", fLabel));
            tHogar.addCell(cellValor(nullSafe(dto.getLocalidad()), fValor));

            doc.add(tHogar);

        } catch (Exception e) {
            // Si algo peta, devolvemos 500
            resp.reset();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generando PDF: " + e.getMessage());
        } finally {
            if (doc.isOpen()) {
                doc.close();
            }
        }
    }

    // ========= Helpers =========

    private static String fmt(Date d) {
        return (d == null) ? "-" : DF.format(d);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static Paragraph espacio(float h) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(h);
        return p;
    }

    private static PdfPCell cellLabel(String txt, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(txt, f));
        c.setBorder(Rectangle.NO_BORDER);
        c.setPaddingBottom(4f);
        return c;
    }

    private static PdfPCell cellValor(String txt, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(nullSafe(txt), f));
        c.setBorder(Rectangle.NO_BORDER);
        c.setPaddingBottom(4f);
        return c;
    }

    private static String nullSafe(String s) {
        return (s == null) ? "-" : s;
    }

    private void addImageCell(PdfPTable table, String rutaEnDB, String caption) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(4f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        try {
            Image img = loadImage(rutaEnDB);
            if (img != null) {
                img.scaleToFit(160, 160);
                img.setAlignment(Image.ALIGN_CENTER);
                cell.addElement(img);

                Paragraph cap = new Paragraph("\n" + caption,
                        FontFactory.getFont(FontFactory.HELVETICA, "Cp1252", true, 9));
                cap.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(cap);
            } else {
                Paragraph cap = new Paragraph(caption + ": (no disponible)",
                        FontFactory.getFont(FontFactory.HELVETICA, "Cp1252", true, 9));
                cap.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(cap);
            }
        } catch (Exception e) {
            Paragraph cap = new Paragraph(caption + ": (error cargando)",
                    FontFactory.getFont(FontFactory.HELVETICA, "Cp1252", true, 9));
            cap.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(cap);
        }

        table.addCell(cell);
    }

    private Image loadImage(String rutaEnDB) throws Exception {
        if (isBlank(rutaEnDB)) return null;

        // 1) ¿es URL absoluta?
        if (rutaEnDB.startsWith("http://") || rutaEnDB.startsWith("https://")) {
            try {
                return Image.getInstance(new URL(rutaEnDB));
            } catch (Exception ignored) { /* fallback siguiente */ }
        }

        // 2) ¿es ruta absoluta existente?
        File f = new File(rutaEnDB);
        if (f.isAbsolute() && f.exists()) {
            return Image.getInstance(f.getAbsolutePath());
        }

        // 3) Asumir que en DB guardaste solo el nombre/relativa -> resolver en BASE_UPLOAD_DIR
        File f2 = new File(BASE_UPLOAD_DIR, rutaEnDB.replace("\\", "/"));
        if (f2.exists()) {
            return Image.getInstance(f2.getAbsolutePath());
        }

        return null; // no encontrada
    }
}
