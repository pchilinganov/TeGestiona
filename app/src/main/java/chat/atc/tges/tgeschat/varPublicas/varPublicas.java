package chat.atc.tges.tgeschat.varPublicas;

import java.util.ArrayList;
import java.util.List;

import chat.atc.tges.tgeschat.model.Message;

public class varPublicas {

    public static String idUsuario_lh="";
    public static String usuario="";
    public static String idVendedorChat="";
    public static String idCanal="";
    public static String nomVendedor="";
    public static String apeVendedor="";
    public static String tokenMovil="";
    public static int dniVendedor=0;

    //Push a chat desde web

    public static String agenteMesaAyuda="";

    //Reestablecimiento contraseñas
    public static int dialogContrasena; // 0: Reestablecimiento | 1: Contraseña Caduca

    //Conexiones
    //public static String URL_DESARROLLO="http://192.168.10.183/ws_siac/";
    //public static String URL_DESARROLLO="http://181.65.211.138:4046/ws_siac/"; //IP PUBLICA
    //public static String URL_DESARROLLO="http://192.168.10.210/chat/lhc_web/index.php?rquest=";
    public static String URL_DESARROLLO = "http://181.65.211.138:8089/chat/lhc_web/webservice/Api_ws.php?rquest=";
    //public static String URL_DESARROLLO = "https://movistartayuda.com/chat/lhc_web/webservice/Api_ws.php?rquest=";
    //181.65.211.138:8089
    public static int estadoHistorialTicket=0;

    public static String URL_DESARROLLO2="http://192.168.10.183/ws_siac/";

    public static int chat_id=0;

    public static List<Message> listaMensajesbandeja=new ArrayList<>();
    //public static List<Message> listaMensajesbandeja=new ArrayList<>();
}