package chat.atc.tges.tgeschat.Mensajes;

public class MensajeDeTexto {
    String nombre;
    private String id;
    private String mensaje;
    private int tipoMensaje;
    private String HoraDelMensaje;
    private String tipoMsgArchivo;
    private String urlArchivo;


    public MensajeDeTexto() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(int tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public String getHoraDelMensaje() {
        return HoraDelMensaje;
    }

    public void setHoraDelMensaje(String horaDelMensaje) {
        HoraDelMensaje = horaDelMensaje;
    }

    public String getTipoMsgArchivo() {
        return tipoMsgArchivo;
    }

    public void setTipoMsgArchivo(String tipoMsgArchivo) {
        this.tipoMsgArchivo = tipoMsgArchivo;
    }

    public String getUrlArchivo() {
        return urlArchivo;
    }

    public void setUrlArchivo(String urlArchivo) {
        this.urlArchivo = urlArchivo;
    }
}
