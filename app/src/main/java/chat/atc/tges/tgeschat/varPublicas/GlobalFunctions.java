package chat.atc.tges.tgeschat.varPublicas;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import chat.atc.tges.tgeschat.BandejaActivity222;
import chat.atc.tges.tgeschat.LoginTelefonica;
import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.VolleyRP;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.fragmentBandejaTicket1;

import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.usuario;

/**
 * Created by rodriguez on 22/06/2018.
 */

public class GlobalFunctions extends BaseVolleyActivity{

    private  VolleyRP volley;
    private  RequestQueue mRequest;
    private static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }

    public static boolean validaSesion(boolean statusSession)
    {
        if (!statusSession){
            logout();
            return sesionFinalizada;
        }
        return sesionFinalizada;
    }

    static boolean  sesionFinalizada=false;

    public static void cancelAllNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancelAll();
    }

    public static void logout(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String estado="";
                sesionFinalizada=true;
                estado="OK";
                cancelAllNotification(context);
                //Toast.makeText(GlobalFunctions.context,"Sesión finalizada.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(get,"Respuesta logout: " + error.toString() , Toast.LENGTH_SHORT).show();
                sesionFinalizada=false;
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idusuario", usuario);
                return params;
            }
        };
        VolleyRP.addToQueue(request,LoginTelefonica.mRequest,context,LoginTelefonica.volley);
    }
}
