package chat.atc.tges.tgeschat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.Services.OnClearFromRecentService;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.dialogs.Contrasena_Restablece_dialog;
import chat.atc.tges.tgeschat.dialogs.IncidenciaMasiva_dialog;
import chat.atc.tges.tgeschat.model.Incidencia;
import chat.atc.tges.tgeschat.network.CustomSSLSocketFactory;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.chat_id;
import static java.security.AccessController.getContext;

/**
 * Created by rodriguez on 17/04/2018.
 */

public class LoginTelefonica  extends BaseVolleyActivity implements OnClickListener
{
    EditText txtUsuario, txtContrasenia;
    Button btnLogin;
    boolean btnSeleccionado=false;
    TextView lblRecuperaContrasena;
    String loginEstado = "",loginMensaje = "";
    JSONObject oUsuario;

    public static FragmentManager fragmentManager;

    public static VolleyRP volley;
    public static RequestQueue mRequest;

    //Incidencia masiva
    List<Incidencia> incidencias = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        //deleteCache(this);
        //clearApplicationData();1
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        fragmentManager= getFragmentManager();

        // To prevent launching another instance of app on clicking app icon
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }

        //Se invoc al servicio

        setContentView(R.layout.layout_login);
        getSupportActionBar().hide(); // oculta barra de acción
        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        varPublicas.tokenMovil = FirebaseInstanceId.getInstance().getToken();


        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtContrasenia = (EditText) findViewById(R.id.txtContrasenia);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        lblRecuperaContrasena = (TextView)findViewById(R.id.lblRecordarContrasena);
        lblRecuperaContrasena.setOnClickListener(this);

        validaciones();

        //variable publica de chat igual a 0 (cuando se cierra sesión por inactividad o manualmente)
        chat_id=0;

        listarIncidencias();
        //handleSSLHandshake(); //Usar en rul de movistartayuda.com

        //Inicia servicio de control de sesión
        Intent intent = new Intent(this, OnClearFromRecentService.class);
        startService(intent);

    }

    @Override
    public void onResume(){
        super.onResume();
        //variable publica de chat igual a 0 (cuando se cierra sesión por inactividad o manualmente)
        chat_id=0;
        //Toast.makeText(LoginTelefonica.this,"Evento onResume de Activity LoginTelefonica" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Toast.makeText(LoginTelefonica.this,"Evento onPause de Activity Logintelefonica" , Toast.LENGTH_SHORT).show();
    }

    public void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            String ok="";
            ok="OK";
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
        }*/
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    private void validaciones(){

        /*---Deshabilita CopyPaste---*/
        txtUsuario.setLongClickable(false);
        txtContrasenia.setLongClickable(false);
        /*------------Fin------------*/

        /*----Validación de usuario y contraseña (mínimo 8 caracteres)------*/
        txtUsuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                @Override
                                                public void onFocusChange(View v, boolean hasFocus) {
                                                    // TODO Auto-generated method stub

                                                    if (hasFocus)
                                                    {
                                                        if (txtUsuario.getText().toString().trim().length() < 8) {     //try using value.getText().length()<3  instead of the value.getText().trim().length()**
                                                            txtUsuario.setError("Mínimo 8 caracteres");
                                                        }
                                                    }
                                                    else
                                                    {
                                                        if (txtUsuario.getText().toString().trim().length() < 8)
                                                        {
                                                            txtUsuario.setError("Mínimo 8 caracteres");
                                                        }
                                                        else
                                                        {
                                                            txtUsuario.setError(null);
                                                        }
                                                    }
                                                }
                                            }
        );

        txtContrasenia.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                    @Override
                                                    public void onFocusChange(View v, boolean hasFocus) {
                                                        // TODO Auto-generated method stub

                                                        if (hasFocus) {
                                                            if (txtContrasenia.getText().toString().trim().length() < 8) {     //try using value.getText().length()<3  instead of the value.getText().trim().length()**
                                                                txtContrasenia.setError("Mínimo 8 caracteres");
                                                            }

                                                        } else {
                                                            if (txtContrasenia.getText().toString().trim().length() < 8) {
                                                                txtContrasenia.setError("Mínimo 8 caracteres");
                                                            } else {
                                                                // your code here
                                                                txtContrasenia.setError(null);
                                                            }
                                                        }
                                                    }
                                                }
        );
        /*----------------------------------------Fin de Validación mínimo 8 caracteres----------------------------------------------*/
    }

    private void listarIncidencias(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "listarIncidencias", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // clear the rv
                incidencias.clear();
                getDataIncidencias(response);
                //Toast.makeText(LoginTelefonica.this,"" + "Existe incidencia: "+  existeIncidenciaMasiva, Toast.LENGTH_LONG).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginTelefonica.this,"Error en listarIncidencias(): " + error.toString() , Toast.LENGTH_SHORT).show();
                //swipeRefreshLayout.setRefreshing(false);
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idvendedor",varPublicas.idVendedorChat);

                if (varPublicas.tokenMovil==null) {
                    varPublicas.tokenMovil ="";
                }
                params.put("token", varPublicas.tokenMovil);
                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }

    String  WS_MsgIncidencia_Msg="";
    Boolean WS_MsgIncidencia_Estado=false;

    JSONArray jsonArrayListaDatos = new JSONArray();
    public void getDataIncidencias (String cadenaJSON){
        try{
            JSONArray jsonArrayDataIncidencias=new JSONArray(cadenaJSON);
            WS_MsgIncidencia_Msg= jsonArrayDataIncidencias.getJSONObject(0).getString("Msg").toString();
            WS_MsgIncidencia_Estado= jsonArrayDataIncidencias.getJSONObject(0).getBoolean("Estado");

            if (jsonArrayDataIncidencias.getJSONObject(0).getJSONArray("ListaDatos")!=null)
            {
                String a="";
                a="es un array";
            }
            else
            {
                String b="";
                b="soy un string";
            }

            jsonArrayListaDatos = jsonArrayDataIncidencias.getJSONObject(0).getJSONArray("ListaDatos");

            if (jsonArrayListaDatos.length() >0){
                existeIncidenciaMasiva=true;
            }

        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }

    //Muestra Dialogo de Búsqueda de Expediente Proactivo
    private void mostrarDialogoIncidenciaMasiva()
    {
        IncidenciaMasiva_dialog myDiag = new IncidenciaMasiva_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "IncidenciaMasiva_dialog");

    }
    public static Boolean existeIncidenciaMasiva=false;
    private void makeRequestLogin(){
        final ProgressDialog progressLogin= new ProgressDialog(this);
        String url = varPublicas.URL_DESARROLLO+"login2";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                getDataUsuario(response);
                Toast.makeText(LoginTelefonica.this,loginMensaje, Toast.LENGTH_LONG).show();


                if(loginEstado.equalsIgnoreCase("8"))
                {
                    varPublicas.dialogContrasena=1; // 1: Contraseña Caduca
                    mostrarDialogRestableceContrasena();
                }

                if(loginEstado.equalsIgnoreCase("4"))
                {
                    Toast.makeText(LoginTelefonica.this,loginMensaje, Toast.LENGTH_LONG).show();
                }

                if(loginEstado.equalsIgnoreCase("99"))
                {
                    Intent intent = new Intent(LoginTelefonica.this, Mensajeria.class);
                    //Intent intent = new Intent(LoginTelefonica.this, BandejaActivity222.class);
                    startActivity(intent);
                }

                onConnectionFinished();
                progressLogin.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginTelefonica.this,"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
                onConnectionFailed(error.toString());
                progressLogin.dismiss();
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                String usuario="", contrasenia="";
                usuario = txtUsuario.getText().toString();
                contrasenia = txtContrasenia.getText().toString();
                Map<String, String> params = new HashMap<String, String>();
                params.put("txtusuario", usuario);
                params.put("txtpassword", contrasenia);
                if (varPublicas.tokenMovil==null) {
                    varPublicas.tokenMovil ="";
                }

                params.put("token", varPublicas.tokenMovil);

                return params;
            }
        };
        addToQueue(request);
        progressLogin.setMessage("Validando Acesso....");
        progressLogin.show();
    }

    public void getDataUsuario (String cadenaJSON){
        try{
            JSONArray jsonArrayDataUsuario=new JSONArray(cadenaJSON);
            loginMensaje= jsonArrayDataUsuario.getJSONObject(0).getString("Msg").toString();
            loginEstado= jsonArrayDataUsuario.getJSONObject(0).getString("NroMensaje").toString();

            JSONArray jsonArrayListaDatos = jsonArrayDataUsuario.getJSONObject(0).getJSONArray("ListaDatos");

            for (int i = 0; i < jsonArrayListaDatos.length(); i++) {
                varPublicas.usuario= jsonArrayListaDatos.getJSONObject(0).getString("idusuario").toString();
                varPublicas.idVendedorChat= jsonArrayListaDatos.getJSONObject(0).getString("id_vendedor").toString();
                varPublicas.nomVendedor= jsonArrayListaDatos.getJSONObject(0).getString("NOMBRES").toString();
                varPublicas.apeVendedor= jsonArrayListaDatos.getJSONObject(0).getString("APELLIDOS").toString();
                varPublicas.dniVendedor= Integer.parseInt(jsonArrayListaDatos.getJSONObject(0).getString("DNI").toString());
                varPublicas.idCanal= jsonArrayListaDatos.getJSONObject(0).getString("id_canal").toString();
            }

            //listarIncidencias();
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }

    private void mostrarDialogRestableceContrasena()
    {
        Contrasena_Restablece_dialog myDiag = new Contrasena_Restablece_dialog();

        myDiag.setCancelable(true);
        myDiag.show(getSupportFragmentManager(), "Contrasena_Restablece");
    }

    /**
     * Enables https connections
     */

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};


            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }



    @Override
    public void onClick(View v) {
        if (v==btnLogin){
            //listarIncidencias();
            makeRequestLogin();
        }

        if (v == lblRecuperaContrasena)
        {
            mostrarDialogRestableceContrasena();
            varPublicas.dialogContrasena = 0; // Diálogo de restablecer contraseña
        }
    }
}
