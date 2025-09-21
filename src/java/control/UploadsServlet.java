package control;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/uploads/*")
public class UploadsServlet extends HttpServlet {

    private static final String BASE_DIR = "C:/icbf_uploads"; // carpeta base en disco

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo(); // ej: /ninos/12/abcd.jpg
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ruta inválida");
            return;
        }

        // quitar "/" inicial
        String relativePath = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;

        // construir ruta segura
        Path requestedFile = Paths.get(BASE_DIR, relativePath).normalize();

if (!requestedFile.startsWith(Paths.get(BASE_DIR))) {
    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
    return;
}

        File file = requestedFile.toFile();
        if (!file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado");
            return;
        }

        // determinar MIME
        String mime = getServletContext().getMimeType(file.getName());
        if (mime == null) mime = "application/octet-stream";
        resp.setContentType(mime);

        resp.setContentLengthLong(file.length());

        try (OutputStream out = resp.getOutputStream()) {
            Files.copy(file.toPath(), out);
        }
    }
}
