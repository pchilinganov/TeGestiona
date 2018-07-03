package chat.atc.tges.tgeschat.dataWS;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;

/**
 * Created by rodriguez on 02/04/2018.
 */

public class DataWSNegocios extends BaseVolleyActivity {

    static Context context;
    static String mensaje;
    static Boolean estado;
    //Reporte Equipos
    static String codProducto;
    //Trazabilidad Gerencial
    static String trazabilidad,flag, grupoOpeComer,medioAcceso, gestorCompromiso, flagRechRein, llave;
    //Detalle Trazabilidad Gerencial
    static String cabecera="",color="";
    //Agendas
    static String tipoGestion_AG, tipoAgenda, grupoOpeComer_AG, flagAgenda;
    //SLA Estaciones
    static String flagEstado, grupoOperComerSLA, fechaLiq, repMedioAcceso;
    static String cabeceraSLA, itemSLA;


    static String nro, fechaVctoSLA, tipoGestion, nombreCliente, segmento, ejecutivoComercial, agrupacionOperacion, producto, peticionRequerimiento, numCD, salesForce, casoDerivado, subEstadoCM, situacionValidado, vector;

    //constructor para Reporte Equipos
    public DataWSNegocios(Context context, String codProducto){
        this.codProducto= codProducto;
        this.context=context;
    }



    /*---Fin de carga de SLA Estaciones*/

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}

