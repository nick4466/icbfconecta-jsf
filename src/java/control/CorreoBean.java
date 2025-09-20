package control;

import dao.CorreoDAO;
import modelo.Usuario;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@ManagedBean(name = "correoBean")
@ViewScoped
public class CorreoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Usuario> padres;              // lista de padres del hogar
    private List<String> correosDestino;       // correos seleccionados
    private String asunto;                     // asunto del correo
    private String mensaje;                    // mensaje del correo

    private final CorreoDAO correoDAO = new CorreoDAO();

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @PostConstruct
    public void init() {
        correosDestino = new ArrayList<>();
        if (sessionBean != null && sessionBean.getUsuarioLogueado() != null) {
            Usuario madre = sessionBean.getUsuarioLogueado();
            Integer hogarId = correoDAO.obtenerHogarMadre(madre.getIdUsuario());
            if (hogarId != null) {
                padres = correoDAO.listarCorreosPadresPorHogar(hogarId);
            }
        }
    }

    // ========================
    // Enviar correo masivo
    // ========================
    public void enviarCorreo() {
        if (correosDestino == null || correosDestino.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Seleccione al menos un padre", null));
            return;
        }

        final String remitente = "icbfconecta04@gmail.com";      
        final String clave = "kjru pfqz fwmp rrzt";             
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session sesion = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message message = new MimeMessage(sesion);
            message.setFrom(new InternetAddress(remitente));

            // Todos los correos seleccionados como destinatarios
            String destinatarios = String.join(",", correosDestino);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatarios));

            message.setSubject(asunto);   // asunto redactado por la madre
            message.setText(mensaje);     // mensaje redactado por la madre

            Transport.send(message);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Correo(s) enviado(s) con éxito a: " + destinatarios, null));

            // Limpiar campos
            correosDestino.clear();
            asunto = "";
            mensaje = "";

        } catch (MessagingException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al enviar: " + e.getMessage(), null));
        }
    }

    // ========================
    // Getters y Setters
    // ========================
    public List<Usuario> getPadres() {
        return padres;
    }

    public List<String> getCorreosDestino() {
        return correosDestino;
    }

    public void setCorreosDestino(List<String> correosDestino) {
        this.correosDestino = correosDestino;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
    