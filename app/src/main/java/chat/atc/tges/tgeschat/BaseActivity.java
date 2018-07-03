package chat.atc.tges.tgeschat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import chat.atc.tges.tgeschat.listeners.LogoutListener;

import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.usuario;

/**
 * Created by rodriguez on 04/01/2018.
 */

public class BaseActivity extends AppCompatActivity implements LogoutListener {
    private VolleyRP volley;
    private RequestQueue mRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();
        ((TGestionaSession)getApplication()).registerSessionListener(this);
        ((TGestionaSession)getApplication()).startUserSession();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((TGestionaSession)getApplication()).onUserInteracted();
    }

    @Override
    public void onSessionLogout() {
        logout();
        finish();
    }

    private void logout(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String ok="";
                ok ="OK";
                Intent intent = new Intent(BaseActivity.this, LoginTelefonica.class);
                startActivity(intent);
                Toast.makeText(BaseActivity.this,"Sesión cerrada por inactividad.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BaseActivity.this,"Respuesta mensajeria: " + error.toString() , Toast.LENGTH_SHORT).show();
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
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }
}
