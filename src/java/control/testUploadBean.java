package control;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.UUID;

@ManagedBean(name = "testUploadBean")
@RequestScoped
public class testUploadBean {

    private String nombres;
    private String apellidos;
    private Part fotoPart;

    // Ajusta la ruta de uploads según tu SO
    private static final String BASE_DIR = "C:/icbf_uploads/tmp";

    public void guardar() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Connection con = null;

        try {
            // 1. Validar archivo
            if (fotoPart == null || fotoPart.getSize() == 0) {
                throw new RuntimeException("Debes seleccionar una foto.");
            }

            // 2. Crear carpeta tmp si no existe
            Files.createDirectories(Paths.get(BASE_DIR));

            // 3. Generar nombre único
            String original = Paths.get(fotoPart.getSubmittedFileName()).getFileName().toString();
            String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')+1) : "";
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String nuevoNombre = uuid + "." + ext;

            // 4. Guardar en disco
            Path destino = Paths.get(BASE_DIR, nuevoNombre);
            try (InputStream in = fotoPart.getInputStream()) {
                Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
            }
             Class.forName("com.mysql.cj.jdbc.Driver");

            // 5. Guardar registro en la BD (tabla ninos, campo foto)
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/conecta_icbf_v2", "root", ""); // ajusta usuario/pass
            String sql = "INSERT INTO ninos (nombres, apellidos, fecha_nacimiento, genero, nacionalidad, hogar_id, padre_id, foto) "
                       + "VALUES (?, ?, '2020-01-01', 'masculino', 'Colombia', 1, 1, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombres);
                ps.setString(2, apellidos);
                ps.setString(3, "ninos/" + nuevoNombre); // ruta relativa
                ps.executeUpdate();
            }

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Niño guardado con foto en la BD", null));

        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error: " + e.getMessage(), null));
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (Exception ignore) {}
        }
    }

    // Getters y Setters
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Part getFotoPart() { return fotoPart; }
    public void setFotoPart(Part fotoPart) { this.fotoPart = fotoPart; }
}
