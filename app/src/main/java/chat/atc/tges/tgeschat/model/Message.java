package chat.atc.tges.tgeschat.model;

public class Message {
    private int id;
    private int canal;
    private String from;
    private String subject;
    private String message;
    private String timestamp;
    private String picture;
    private String msgNoLeido;
    private String estado;
    private String estadoEncuesta;
    private String nroConsulta;
    private String nroTickets;
    private boolean isImportant;
    private boolean isRead;

    private int color = -1;

    private int idChat;

    public Message() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCanal() {
        return canal;
    }

    public void setCanal(int canal) {
        this.canal = canal;
    }

    public int getIdChat() {
        return idChat;
    }

    public void setIdChat(int idChat) {
        this.idChat = idChat;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getMsgNoLeido() {
        return msgNoLeido;
    }

    public void setMsgNoLeido(String msgNoLeido) {
        this.msgNoLeido = msgNoLeido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoEncuesta() {
        return estadoEncuesta;
    }

    public void setEstadoEncuesta(String estadoEncuesta) {
        this.estadoEncuesta = estadoEncuesta;
    }

    public String getNroConsulta() {
        return nroConsulta;
    }

    public void setNroConsulta(String nroConsulta) {
        this.nroConsulta = nroConsulta;
    }

    public String getNroTickets() {
        return nroTickets;
    }

    public void setNroTickets(String nroTickets) {
        this.nroTickets = nroTickets;
    }
}
