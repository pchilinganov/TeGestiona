package chat.atc.tges.tgeschat.Mensajes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.atc.tges.tgeschat.BandejaActivity222;
import chat.atc.tges.tgeschat.BaseActivity;
import chat.atc.tges.tgeschat.LoginTelefonica;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.VolleyRP;
import chat.atc.tges.tgeschat.dialogs.Encuesta_dialog;
import chat.atc.tges.tgeschat.dialogs.IncidenciaMasiva_dialog;
import chat.atc.tges.tgeschat.dialogs.Descargas_dialog;
import chat.atc.tges.tgeschat.upload.MultipartUtility;
import chat.atc.tges.tgeschat.varPublicas.GlobalFunctions;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
//import okhttp3.Request;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.*;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;

public class Mensajeria extends BaseActivity implements  OnClickListener{

    public static final String MENSAJE = "MENSAJE";
    public static String idChat;
    private BroadcastReceiver bR;
    private ImageButton imgBtnAdjuntar;
    private RecyclerView  rv;
    TextView tvFileName;
    private ImageButton bTEnviarMensaje;
    private EditText eTEscribirMensaje;
    private EditText eTRECEPTOR;
    private List<MensajeDeTexto> mensajeDeTextos;
    private MensajeriaAdapter adapter;
    public static FragmentManager fragmentManager;
    PowerManager.WakeLock wakeLock;
    private String selectedFilePath;
    ProgressDialog dialog;
    private String MENSAJE_ENVIAR = "";
    private String idTicket = "";
    private String RECEPTOR;
    private VolleyRP volley;
    private RequestQueue mRequest;
    String mensaje = "";
    String estadoEncuestaBusqXConsulta=""; // indicador de si el ticket solicitado por pestaña "Consulta " tiene encuesta pendiente
    private MenuItem mIAdjuntar;

    //Permiso para acceder a Escritura y lectura de ficheros (Android M)
    private final int REQUEST_PERMISSION_PHONE_READ =1;
    private final int REQUEST_PERMISSION_PHONE_WRITE =2;
    private final int REQUEST_PERMISSION_RECORD =3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mensajeria);
        mensajeDeTextos = new ArrayList<>();
        fragmentManager= getFragmentManager();
        Intent extras = getIntent();
        Bundle bundle = extras.getExtras();
        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        estadoEncuestaBusqXConsulta="";

        //Obtiene token de vendedor
        varPublicas.tokenMovil = FirebaseInstanceId.getInstance().getToken();
        if (varPublicas.tokenMovil==null) {
            varPublicas.tokenMovil ="";
        }

        chat_id=0;

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chat");

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //mIAdjuntar = menu.find(R.id.action_Adjuntar);
        bTEnviarMensaje = (ImageButton) findViewById(R.id.bTenviarMensaje);
        bTEnviarMensaje.setOnClickListener(this);
        eTEscribirMensaje = (EditText) findViewById(R.id.eTEsribirMensaje);
        eTEscribirMensaje.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Actualiza mensajes leídos
                if (chat_id>0){
                    //if(idTicket!= null && Integer.valueOf(idTicket)!=0)   // debe borrar las notificaciones de un ticket determinado
                        //cancelNotification(getApplicationContext(), Integer.valueOf(idTicket));
                    WSMensajesLeidos();
                }
                return false;
            }
        }
        );

        //imgBtnAdjuntar = (ImageButton) findViewById(R.id.imgBtnClip);
        //imgBtnAdjuntar.setOnClickListener(this);

        // eTRECEPTOR = (EditText) findViewById(R.id.receptorET);

        rv = (RecyclerView) findViewById(R.id.rvMensajes);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        rv.setLayoutManager(lm);

        adapter = new MensajeriaAdapter(mensajeDeTextos,this);
        rv.setAdapter(adapter);

        bTEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mensaje = validarCadena(eTEscribirMensaje.getText().toString());
                //RECEPTOR = eTRECEPTOR.getText().toString();
                //if(!mensaje.isEmpty() && !RECEPTOR.isEmpty()){
                if(!mensaje.isEmpty() ){
                    MENSAJE_ENVIAR = mensaje;
                    MandarMensaje(); //en web service , recibir datos de hora de servidor ,etc


                    //if (chat_id==0) { //Para primer mensaje
                        try
                        {
                            bTEnviarMensaje.setEnabled(false);
                            if (chat_id==0) {
                                Thread.sleep(1300);
                            }
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }

                    //}

                }
            }
        }
        );
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        setScrollbarChat();

        bR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Se valida encuesta
                String estadoEncuesta= intent.getStringExtra("key_encuesta");
                String estadoIM=intent.getStringExtra("key_IM");
                if (estadoEncuesta!=null && estadoEncuesta.equalsIgnoreCase("OK"))
                {
                    int chatIdPushEncuesta=0;
                    chatIdPushEncuesta= Integer.parseInt(intent.getStringExtra("key_idChat"));
                    if (chatIdPushEncuesta==chat_id){
                        //validar si el chat recibido por push es igual al actual y mostrar si es así, en otro caso, mostrar mensaje de nueva encuest pendiente.
                        mostrarDialogoEncuesta();
                    } else{
                        //Toast.makeText(Mensajeria.this,"Chat: " + chatIdPushEncuesta + " ha lanzado una encuesta de satisfacción", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(estadoIM!=null && estadoIM.equalsIgnoreCase("OK")){
                    mostrarDialogoIncidenciaMasiva();
                }
                else
                {
                    String mensaje = intent.getStringExtra("key_mensaje");
                    String hora = intent.getStringExtra("key_hora"); //
                    String idChatRecibidoPorPush = intent.getStringExtra("key_idChat");
                    String tipoMsgArchivo = intent.getStringExtra("key_tipoMsg");
                    String urlArchivo = intent.getStringExtra("key_urlArchivo");
                    String ticket = intent.getStringExtra("key_idTicket");
                    idTicket=ticket;

                    //si el idChat enviado por push es diferente al idChat del chat actual. Sólo aparecerá push de otro chat con otro agente
                    /*if (Integer.parseInt(idChatRecibidoPorPush) != chat_id ) {
                        //varPublicas.chat_id = Integer.parseInt(idChatRecibidoPorPush); // se actualiza el idChat
                        //RecibirHistorialTicket();
                        String string="";
                        string="OK";
                        //Toast.makeText(Mensajeria.this,"IDchat enviado por push es diferente del idchat actual y chat id!=0", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(Mensajeria.this,"idChatRecibidoPorPush: " + idChatRecibidoPorPush, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(Mensajeria.this,"chat_id: " + chat_id, Toast.LENGTH_SHORT).show();
                    }
                    else */
                    if (Integer.parseInt(idChatRecibidoPorPush) == chat_id || chat_id==0) //Si el IdChat enviado por push es igual al idchat del chat actual, o si el idChat actual es 0 [nuevo registro] , que aparezca el mensaje de chat en pantalla
                    {
                        CreateMensaje(mensaje, hora, 2, agenteMesaAyuda, tipoMsgArchivo, urlArchivo);
                    }
                    if (ticket!=null){
                        //getSupportActionBar().setTitle("MESA DE AYUDA #T-" + ticket);
                        getSupportActionBar().setSubtitle("Tickets # "+ ticket);//  .setTitle("MESA DE AYUDA #T-" + ticket);
                    }
                } // aquí se agrega otro br en caso haya
            }
        }
        ;

        //Retorna historial de chat
        if (estadoHistorialTicket==1){
                if(bundle!=null){ //este codigo responde al presionar el push de un mensaje nuevo
                    String idChatRecibidoPorPush="", estadoTicket="";
                    idTicket = bundle.getString("key_idTicket");
                    idChatRecibidoPorPush = bundle.getString("key_idChat");
                    estadoTicket = bundle.getString("key_Estado");
                    estadoEncuestaBusqXConsulta= bundle.getString("key_EstadoEncuestaConsulta");

                    if (Integer.parseInt(idChatRecibidoPorPush) != chat_id){ //si el idChat enviado por push es diferente al idChat del chat actual
                        chat_id = Integer.parseInt(idChatRecibidoPorPush); // se actualiza el idChat
                        //RecibirHistorialTicket();
                    }
                    else
                    {
                        //RecibirHistorialTicket();
                    }

                    //Verifica estado de ticket
                    if (estadoTicket!=null) {
                        if (estadoTicket.equalsIgnoreCase("Cerrado")) {
                            /*eTEscribirMensaje.setEnabled(false);
                            bTEnviarMensaje.setEnabled(false);
                            eTEscribirMensaje.setHint("Ticket Cerrado");*/
                        } else if (estadoTicket.equalsIgnoreCase("Abierto")) {
                            /*eTEscribirMensaje.setEnabled(true);
                            bTEnviarMensaje.setEnabled(true);*/
                        }
                    }
                    getSupportActionBar().setTitle("Chat");
                    getSupportActionBar().setSubtitle("Ticket " + idTicket);
                }
        }

        idChat =String.valueOf(chat_id);

        if (chat_id>0){
            WSMensajesLeidos();
        }

        //incidencia masiva

        //Progunta por permisos de lectura
        //verifyStoragePermissions(this);

    }

    private void mostrarDialogoEncuesta()
    {
        Encuesta_dialog myDiag = new Encuesta_dialog();
        myDiag.setCancelable(true);
        myDiag.show(fragmentManager, "Encuesta_dialog");
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mIAdjuntar = menu.findItem(R.id.action_Adjuntar);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //no inspection SimplifiableIfStatement
        /*if (id == R.id.action_Test) {
            //RecibirHistorialTicket();
            //imageBrowse();
            //openFolder();
            mostrarDialogoEncuesta();
            return true;
        }*/

        if (id == R.id.action_NuevoChat) {
            //RecibirHistorialTicket();
            Intent intent = new Intent(Mensajeria.this, Mensajeria.class);
            startActivity(intent);
            chat_id = 0;
            return true;
        }

        if (id == R.id.action_Notificaciones) {
            mostrarDialogoIncidenciaMasiva();
            return true;
        }

        if (id == R.id.action_Descargas) {
            mostrarDialogoDescargas();
            return true;
        }

        if (id == R.id.action_BandejaTickets) {
            Intent intent = new Intent(Mensajeria.this, BandejaActivity222.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_Adjuntar) {
            //Solicita Permiso para accedesr a archivos

            //Elige archivo de explorador
            if (chat_id==0) {
                Toast.makeText(Mensajeria.this,"No se puede enviar un archivo adjunto como primer mensaje de chat.", Toast.LENGTH_LONG).show();
            }else{
                showPhoneStatePermission();

            }

            return true;
        }

        if (id == R.id.action_CerrarSesion) {
            logout();
            Intent intent = new Intent(Mensajeria.this, LoginTelefonica.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoIncidenciaMasiva()
    {
        IncidenciaMasiva_dialog myDiag = new IncidenciaMasiva_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "IncidenciaMasiva_dialog");
        LoginTelefonica.existeIncidenciaMasiva=false;
    }

    private void mostrarDialogoDescargas()
    {
        Descargas_dialog myDiag = new Descargas_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "Descargas_dialog");
    }

    private String validarCadena(String cadena){
        for(int i=0;i<cadena.length();i++) if(!(""+cadena.charAt(i)).equalsIgnoreCase(" "))
            return cadena.substring(i,cadena.length());
        return "";
    }
    int chat=0;



    private void MandarMensaje(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "recibirChat", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getDataMensajeEnviado(response); //obtener datos de servidor (hora, etc)
                bTEnviarMensaje.setEnabled(true);
                //RecibirHistorialTicket();

                //Toast.makeText(Mensajeria.this,"ID VENDEDOR: " + varPublicas.idVendedorChat, Toast.LENGTH_LONG).show();
                eTEscribirMensaje.setText("");

                //if (chat_id!=0 ) {
                    /*if(idTicket!= null && Integer.valueOf(idTicket)!=0)
                    cancelNotification(getApplicationContext(), Integer.valueOf(idTicket));*/
                //}
                //Toast.makeText(Mensajeria.this,response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mensajeria.this,"Respuesta mensajeria: " + error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idvendedor", idVendedorChat);
                params.put("imei","0");
                params.put("latitud","0");
                params.put("longitud","0");
                params.put("token", tokenMovil);
                params.put("mensaje",MENSAJE_ENVIAR);
                //params.put("id_chat","0");
                if (chat_id>0) //ya hay un chat abierto
                    chat=-1;

                if (chat==0) // chat <=0
                    params.put("id_chat",String.valueOf(0));
                else
                    params.put("id_chat",String.valueOf(chat_id));

                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }
    String  WS_MsgEnviado_Msg="";
    Boolean WS_MsgEnviado_Estado=false;
    Boolean WS_Session_Msg=false;

    public void getDataMensajeEnviado (String cadenaJSON)
    {
        try
        {
            String mensaje="", fechaHora="";
            JSONArray jsonArrayDataUsuario=new JSONArray(cadenaJSON);
            WS_MsgEnviado_Msg= jsonArrayDataUsuario.getJSONObject(0).getString("Msg").toString();
            WS_MsgEnviado_Estado= jsonArrayDataUsuario.getJSONObject(0).getBoolean("Estado");
            WS_Session_Msg= jsonArrayDataUsuario.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

            if (WS_Session_Msg!=null && !WS_Session_Msg)
            {
                GlobalFunctions.validaSesion(WS_Session_Msg);
                Intent intent = new Intent(this, LoginTelefonica.class);
                startActivity(intent);
                Toast.makeText(this, "Sesión finalizada.", Toast.LENGTH_SHORT).show();
                return;
            }

            mensaje= jsonArrayDataUsuario.getJSONObject(0).getString("mensaje").toString();
            fechaHora= jsonArrayDataUsuario.getJSONObject(0).getString("fecha_hora").toString();

            if (chat_id==0) {
                chat_id = jsonArrayDataUsuario.getJSONObject(0).getInt("id_chat");
                idTicket = jsonArrayDataUsuario.getJSONObject(0).getString("idticket");
            }

            CreateMensaje(mensaje, fechaHora, 1, nomVendedor, "text","");
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }

    private void logout(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String estado="";
                estado="OK";
                GlobalFunctions.cancelAllNotification(getApplicationContext());
                //Toast.makeText(BandejaActivity.this,response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mensajeria.this,"Respuesta mensajeria: " + error.toString() , Toast.LENGTH_SHORT).show();
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

    private void RecibirHistorialTicket(){
        mensajeDeTextos.clear();
        rv.removeAllViews();
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "listarChatTicket", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                eTEscribirMensaje.setText(""); //limpia la ventana de chat
                getDataHistorial(response);
                setScrollbarChat(); //mantiene el chat en el último mensaje enviado o recibido
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mensajeria.this,"Respuesta mensajeria: " + error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ticket",idTicket);
                params.put("token", tokenMovil);
                params.put("id_chat",""+ chat_id);
                params.put("idvendedor", idVendedorChat);
                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }

    String  WS_MsgHistorial_Msg="";
    Boolean WS_MsgHistorial_Estado=false;
    JSONArray jsonArrayListaDatos= new JSONArray();

    public void getDataHistorial (String cadenaJSON){
        try{
            String estadoEncuesta="", nroTickets="", estado_chat="",nroVendedorTicker="";
            JSONArray jsonArrayDataHistorial=new JSONArray(cadenaJSON);
            WS_MsgHistorial_Msg= jsonArrayDataHistorial.getJSONObject(0).getString("Msg").toString();
            WS_MsgHistorial_Estado= jsonArrayDataHistorial.getJSONObject(0).getBoolean("Estado");
            WS_Session_Msg= jsonArrayDataHistorial.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

            if (WS_Session_Msg!=null && !WS_Session_Msg)
            {
                GlobalFunctions.validaSesion(WS_Session_Msg);
                Intent intent = new Intent(this, LoginTelefonica.class);
                startActivity(intent);
                Toast.makeText(this, "Sesión finalizada.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (  estadoEncuestaBusqXConsulta!= null && estadoEncuestaBusqXConsulta.length()>0){
                estadoEncuesta = estadoEncuestaBusqXConsulta;
            }else{
                estadoEncuesta = jsonArrayDataHistorial.getJSONObject(0).getString("enviar_encuesta");
                estadoEncuestaBusqXConsulta="";
            }

            nroTickets= jsonArrayDataHistorial.getJSONObject(0).getString("tickets").toString(); //nro de tickets asociados al chat
            estado_chat=jsonArrayDataHistorial.getJSONObject(0).getString("estado_chat").toString(); //estado de chat : finalizado o no
            nroVendedorTicker=jsonArrayDataHistorial.getJSONObject(0).getString("id_vendedor").toString(); //estado de chat : finalizado o no

            jsonArrayListaDatos = jsonArrayDataHistorial.getJSONObject(0).getJSONArray("ListaDatos");
            if(mensajeDeTextos.size() ==0) { //Valida si array tiene elementos anteriores, con tal de no repetir la información
                String mensajeHistorial="", fechaHora="", nombre="",tipo="", tipoMsg="", urlArchivo="";
                int idPropietario=0, tipoMensaje=0;
                for (int i = 0; i < jsonArrayListaDatos.length(); i++) {
                    mensajeHistorial= jsonArrayListaDatos.getJSONObject(i).getString("mensaje").toString();
                    fechaHora= jsonArrayListaDatos.getJSONObject(i).getString("hora").toString();
                    nombre= jsonArrayListaDatos.getJSONObject(i).getString("nombres").toString();
                    tipo=jsonArrayListaDatos.getJSONObject(i).getString("tipo").toString();
                    tipoMsg=jsonArrayListaDatos.getJSONObject(i).getString("tipo_msg").toString();
                    urlArchivo=jsonArrayListaDatos.getJSONObject(i).getString("url").toString();

                    //idPropietario= jsonArrayListaDatos.getJSONObject(i).getInt("id_propietario");
                    //chat_id= jsonArrayListaDatos.getJSONObject(i).getInt("id_chat");

                    if (tipo.equalsIgnoreCase("vendedor"))
                        tipoMensaje=1;
                    else if (tipo.equalsIgnoreCase("agente"))
                        tipoMensaje=2;

                    //Toast.makeText(Mensajeria.this,"mensajeHistorial" + mensajeHistorial + "fecha Hora" + fechaHora+ "tipo " + tipo + "tipomensaje " + tipoMensaje, Toast.LENGTH_LONG).show();
                    CreateMensaje(mensajeHistorial, fechaHora, tipoMensaje,nombre, tipoMsg, urlArchivo);
                }
            }
            //mostra encuesta en caso esté pendiente
            if (estadoEncuesta.equalsIgnoreCase("5")){
                mostrarDialogoEncuesta();
            }
            //Actualiza barra de título mostrando los tickets pertenecientes al chat actual
            if (nroTickets!=null){
                getSupportActionBar().setTitle("Chat");
                getSupportActionBar().setSubtitle("Tickets # " + nroTickets);
            }
            //valida que chats finalizados estén inhabilitados para la interacción
            if (estado_chat.equalsIgnoreCase("finalizado"))
            {
                //inhabilitar chat
                eTEscribirMensaje.setEnabled(false);
                bTEnviarMensaje.setEnabled(false);
                eTEscribirMensaje.setHint("Chat Finalizado");
                eTEscribirMensaje.setVisibility(View.GONE);
                bTEnviarMensaje.setVisibility(View.GONE);
                mIAdjuntar.setVisible(false);
                return;


            }
            else
            {
                eTEscribirMensaje.setEnabled(true);
                bTEnviarMensaje.setEnabled(true);
                eTEscribirMensaje.setHint("");
                eTEscribirMensaje.setFocusable(true);
                eTEscribirMensaje.setVisibility(View.VISIBLE);
                bTEnviarMensaje.setVisibility(View.VISIBLE);
                mIAdjuntar.setVisible(true);
            }
            //valida que chat de un vendedor no pueda ser mofificado por otro
            if (!nroVendedorTicker.equalsIgnoreCase(varPublicas.idVendedorChat))
            {
                //inhabilitar chat
                eTEscribirMensaje.setEnabled(false);
                bTEnviarMensaje.setEnabled(false);
                eTEscribirMensaje.setHint("Chat ajeno");
                eTEscribirMensaje.setVisibility(View.GONE);
                bTEnviarMensaje.setVisibility(View.GONE);
                mIAdjuntar.setVisible(false);
                return;
            }
            else
            {
                eTEscribirMensaje.setEnabled(true);
                bTEnviarMensaje.setEnabled(true);
                eTEscribirMensaje.setVisibility(View.VISIBLE);
                bTEnviarMensaje.setVisibility(View.VISIBLE);
                //eTEscribirMensaje.setHint("Chat ajeno");
                mIAdjuntar.setVisible(true);

            }
        }
        catch(JSONException je)
        {
            je.printStackTrace();
        }
    }

    private void WSMensajesLeidos(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "mensajeLeido", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                //getDataMensajeEnviado(response); //obtener datos de servidor (hora, etc)
                String resultado="";
                resultado="OK";
                //Toast.makeText(Mensajeria.this,"Mensajes Leídos", Toast.LENGTH_LONG).show();
                //eTEscribirMensaje.setText("");

                //Toast.makeText(Mensajeria.this,response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mensajeria.this,"Error de mensajes Leidos: " + error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idvendedor", idVendedorChat);
                params.put("token", tokenMovil);
                params.put("idchat",""+chat_id);

                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }

    public void CreateMensaje(String mensaje, String hora, int tipoDeMensaje, String nombre, String tipoMsgArchivo, String urlArchivo){
        MensajeDeTexto mensajeDeTextoAuxiliar = new MensajeDeTexto();
        mensajeDeTextoAuxiliar.setId("0");
        mensajeDeTextoAuxiliar.setNombre(nombre);
        mensajeDeTextoAuxiliar.setMensaje(mensaje);
        mensajeDeTextoAuxiliar.setTipoMensaje(tipoDeMensaje);
        mensajeDeTextoAuxiliar.setHoraDelMensaje(hora);
        mensajeDeTextoAuxiliar.setTipoMsgArchivo(tipoMsgArchivo);
        mensajeDeTextoAuxiliar.setUrlArchivo(urlArchivo);
        mensajeDeTextos.add(mensajeDeTextoAuxiliar);
        adapter.notifyDataSetChanged();
        setScrollbarChat(); //mantiene el chat en el último mensaje enviado o recibido
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bR);
        //Actualiza mensajes leídos
        if (chat_id>0){
            WSMensajesLeidos();
        }
        //Toast.makeText(Mensajeria.this,"Evento onPause de Activity Mensajeria" , Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bR,new IntentFilter(MENSAJE));
        //Actualiza mensajes leídos
        if (chat_id>0){
            WSMensajesLeidos();
        }

        //Reestablece todos los mensajes del chat
        if (chat_id>0){
            RecibirHistorialTicket();
        }

        //Verifica si hay incidencia masiva y la muestra cuando el usuario se logea
        if (LoginTelefonica.existeIncidenciaMasiva){
            mostrarDialogoIncidenciaMasiva();
        }
    }

    /*
    * @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(BandejaActivity222.this,"Evento onResume de Activity Bandeja" , Toast.LENGTH_SHORT).show();
    }
*/
    /*@Override
    public void onPause() {
        super.onPause();
        Toast.makeText(Mensajeria.this,"Evento onPause de Activity Mensajeria" , Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(Mensajeria.this,"Evento onDestroy de Activity Mensajeria" , Toast.LENGTH_SHORT).show();
    }

    public void setScrollbarChat(){
        rv.scrollToPosition(adapter.getItemCount()-1);
    }

    //uploadd

    public static String BASE_URL = URL_DESARROLLO +"adjuntarArchivo";
    static final int PICK_IMAGE_REQUEST = 1;
    String filePath;

    /*private void imageBrowse() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if(requestCode == PICK_IMAGE_REQUEST){
                Uri picUri = data.getData();

                filePath = getPath(picUri);

                Log.d("picUri", picUri.toString());
                Log.d("filePath", filePath);

                subirAServidor(filePath);
                //imageView.setImageURI(picUri);
            }

            if(requestCode == FILE_SELECT_CODE){
                Uri uri = data.getData();
                //Log.d(TAG, "File Uri: " + uri.toString());
                // Get the path
                String path = getPathDocument(uri);

                subirAServidor(path);
                //Log.d(TAG, "File Path: " + path);
            }
        }
    }*/

    /*private void subirAServidor(String filePath) {
        try {
            final File rutaFotoCamaraGaleria= new File(filePath);
            //filenameGaleria = getFilename();
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(this.getApplicationContext(), uploadId, BASE_URL)
                    .addFileToUpload(rutaFotoCamaraGaleria.getPath(), "archivo")
                    .addParameter("idvendedor", idVendedorChat)
                    .addParameter("idchat", "501")
                    .addParameter("token", tokenMovil)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(1)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(UploadInfo uploadInfo) {}

                        @Override
                        public void onError(UploadInfo uploadInfo, Exception e) {
                            Toast.makeText(getApplicationContext(),"Error de subida: " + e.toString(),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                            //ELiminar imagen
                            //ELiminar imagen
                            /*File eliminar = new File(rutaFotoCamaraGaleria.getPath());
                            if (eliminar.exists()) {
                                if (eliminar.delete()) {
                                    System.out.println("archivo eliminado:" + rutaFotoCamaraGaleria.getPath());
                                } else {
                                    System.out.println("archivo no eliminado" + rutaFotoCamaraGaleria.getPath());
                                }
                            }
                            Toast.makeText(getApplicationContext(),"Imagen subida exitosamente.",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(UploadInfo uploadInfo) {
                            Toast.makeText(getApplicationContext(),"Subida de imagen cancelada.",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .startUpload();

        } catch (Exception exc) {
            System.out.println(exc.getMessage()+" "+exc.getLocalizedMessage());
        }
    }*/

    private String getPath(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private String getPathDocument(Uri contentUri) {
        String[] proj = { MediaStore.Files.FileColumns.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private static final int PICK_FILE_REQUEST = 1;
    private void showFileChooser() {

        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }

                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
                wakeLock.acquire();

                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this, selectedFileUri);
                //Log.i(TAG, "Selected File Path:" + selectedFilePath);

                if (selectedFilePath != null && !selectedFilePath.equals(""))
                {
                    //tvFileName.setText(selectedFilePath);
                    subirFichero();

                } else
                {
                    Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void subirFichero(){
        //on upload button Click
        if (selectedFilePath != null) {
            dialog = ProgressDialog.show(Mensajeria.this, "", "Subiendo archivo...", true);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        //creating new thread to handle Http Operations
                        //uploadFile(selectedFilePath);

                        //Ejecuta tarea asíncrona
                        MiTareaAsincrona mta = new MiTareaAsincrona();
                        mta.execute();

                        //subirOkClient(selectedFilePath);
                    } catch (OutOfMemoryError e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Mensajeria.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                    }

                }
            }).start();
        } else {
            Toast.makeText(Mensajeria.this, "Please choose a File First", Toast.LENGTH_SHORT).show();
        }
    }

    private void subirOkClient(String selectedFilePath){
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
        RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"archivo\"; filename=\""+selectedFilePath+"\"\r\nContent-Type: text/plain\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"token\"\r\n\r\nfmnBXnzzt04:APA91bFm3we-AkdAvsin3ezRGFiOQQSvsudNWRYCTVcy8OrkPQxh8gkbf6vWvunuZhY9flDtRWcogaBcE1c-SzpkLpI_gz5zuoCGg6XI4VXt59b1hzgchGoR5NOUiQ4LOLeMXfDYFW18\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"idvendedor\"\r\n\r\n9\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"idchat\"\r\n\r\n501\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://192.168.10.210/chat/lhc_web/webservice/Api_ws.php?rquest=adjuntarArchivo")
                .post(body)
                .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "453955c4-33f0-af35-e5cd-e4195f975579")
                .build();
        okhttp3.Response response;
        try {
            response = client.newCall(request).execute();
            int i=1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
    }

    public int uploadFile(final String selectedFilePath) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(varPublicas.URL_DESARROLLO + "adjuntarArchivo");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("archivo",selectedFilePath);
                /*connection.setRequestProperty("idchat","841");
                connection.setRequestProperty("idvendedor",varPublicas.idVendedorChat);
                connection.setRequestProperty("token",varPublicas.tokenMovil);*/

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"archivo\";filename=\""
                        + selectedFilePath + "\"" + lineEnd + "Content-Type: image/png");

                /*dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"archivo\";filename=\""
                        + selectedFile + "\"" + lineEnd + "Content-Type: image/png\r\n\r\n\r\n" +twoHyphens + boundary + lineEnd+ "Content-Disposition: form-data; name=\"token\"\r\n\r\nfmnBXnzzt04:APA91bFm3we-AkdAvsin3ezRGFiOQQSvsudNWRYCTVcy8OrkPQxh8gkbf6vWvunuZhY9flDtRWcogaBcE1c-SzpkLpI_gz5zuoCGg6XI4VXt59b1hzgchGoR5NOUiQ4LOLeMXfDYFW18\r\n"+twoHyphens + boundary + lineEnd+"Content-Disposition: form-data; name=\"idvendedor\"\r\n\r\n9\r\n"+twoHyphens + boundary + lineEnd+"Content-Disposition: form-data; name=\"idchat\"\r\n\r\n841\r\n"+twoHyphens + boundary + lineEnd);*/

                //dataOutputStream.writeBytes(twoHyphens);
/*
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"idchat\""
                        + lineEnd  + "841"  );

                dataOutputStream.writeBytes(lineEnd);

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"token\""
                        + lineEnd  + varPublicas.tokenMovil  );

                dataOutputStream.writeBytes(lineEnd);

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"idvendedor\""
                        + lineEnd   + varPublicas.idVendedorChat  );

                dataOutputStream.writeBytes(lineEnd);

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);*/

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    try {
                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer, 0, bufferSize);
                    } catch (OutOfMemoryError e) {
                        Toast.makeText(Mensajeria.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                String respuestaServidor = "";
                //DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
                InputStreamReader dataInputStreamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferReader = new BufferedReader(dataInputStreamReader);


                StringBuilder response = new StringBuilder();
                try{
                    String line = "";
                    while((line = bufferReader.readLine()) != null) {
                        response.append(line);
                    }

                    serverResponseCode = connection.getResponseCode();
                    respuestaServidor = dataInputStreamReader.toString();

                    /*while ((dataInputStreamReader.readLine()) != null) {
                        /*os.writeBytes(userInput);
                        os.writeByte('\n');
                        respuestaServidor +=  respuestaServidor + dataInputStreamReader.readLine();
                    }*/
                    //respuestaServidor =connection.ge;
                }catch (OutOfMemoryError e){
                    Toast.makeText(Mensajeria.this, "Memory Insufficient!", Toast.LENGTH_SHORT).show();
                }
                String serverResponseMessage = connection.getResponseMessage();

                //Log.i(TAG, "Server Response is: " + respuestaServidor + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Mensajeria.this, "Archivo subido correctamente", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

                if (wakeLock.isHeld()) {

                    wakeLock.release();
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Mensajeria.this, "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Mensajeria.this, "URL Error!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Mensajeria.this, "Cannot Read/Write File", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialog.dismiss();
            return serverResponseCode;
        }

    }

    //Asynctask para subir archivos
    String nombreArchivo="", urlAdjuntar="", horaAdjuntar="";
    private class MiTareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            MultipartUtility mpu = new MultipartUtility();
            //Log.e("MSG", "inicio");
            try {

                //String filewPathStr="/storage/emulated/0/DCIM/Screenshots/Screenshot_20180503-081707.png";

                File filePath= new File(selectedFilePath);
                nombreArchivo =filePath.getName();
                mpu.MultipartUtilityV2(URL_DESARROLLO + "adjuntarArchivo");
                mpu.addFilePart("archivo", filePath);
                mpu.addFormField("idvendedor",varPublicas.idVendedorChat);
                mpu.addFormField("idvendedor",varPublicas.idVendedorChat);
                mpu.addFormField("idchat",""+chat_id);
                mpu.addFormField("idchat",""+chat_id);

                String response = mpu.finish();

                JSONArray jsonArrayDataAdjunto=new JSONArray(response);
                WS_MsgHistorial_Msg= jsonArrayDataAdjunto.getJSONObject(0).getString("Msg").toString();
                WS_MsgHistorial_Estado= jsonArrayDataAdjunto.getJSONObject(0).getBoolean("Estado");
                urlAdjuntar = jsonArrayDataAdjunto.getJSONObject(0).getString("url");
                horaAdjuntar = jsonArrayDataAdjunto.getJSONObject(0).getString("hora");


                //Log.e("MSG", response);
            } catch (IOException e) {
                //Log.e("Error", "Exception xd: " + e.getLocalizedMessage());
            } catch (JSONException e){
                Toast.makeText(Mensajeria.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            Toast.makeText(Mensajeria.this,"Se subió el archivo correctamente", Toast.LENGTH_LONG).show();
            //String mensaje="Se adjunto el archivo " + nombreArchivo;

            //MENSAJE_ENVIAR = mensaje;
            //MandarMensaje(); //en web service , recibir datos de hora de servidor ,etc
            CreateMensaje(nombreArchivo,horaAdjuntar,1,varPublicas.nomVendedor,"file",urlAdjuntar);
            //public void CreateMensaje(String mensaje, String hora, int tipoDeMensaje, String nombre){

        }

        @Override
        protected void onCancelled() {
            dialog.dismiss();
        }
    }


    @Override
    public void onClick(View v) {
        if (v==imgBtnAdjuntar){
            showFileChooser();
            //openFolder();
            //imageBrowse();
        }
    }
    
    // Permisos solicitados para escribir , leer archivos o imágenes de dispositivo

    private void showPhoneStatePermission() {
        //int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permissionCheckReadExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        //int permissionRecord = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);

        //Permiso de Escritura
        /*if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Mensajeria.this, WRITE_EXTERNAL_STORAGE)) {
                showExplanation("Permiso necesario", "Para continuar con el uso del sistema ", WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_WRITE);
            } else {
                requestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_WRITE);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Permiso Escritura ya otorgado!", Toast.LENGTH_SHORT).show();
        }*/

        //Permiso de Lectura
        if (permissionCheckReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Mensajeria.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showExplanation("Permiso necesario", "Para continuar con el envío de archivos, otorgar permisos de lectura.", Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_READ);
            } else {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_READ);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permiso Lectura ya otorgado!", Toast.LENGTH_SHORT).show();
            showFileChooser();
        }

        //Permiso de Grabación

        /*if (permissionRecord != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Mensajeria.this,Manifest.permission.RECORD_AUDIO)) {
                showExplanation("Permiso necesario", "Para continuar con el uso del sistema ", Manifest.permission.RECORD_AUDIO, REQUEST_PERMISSION_RECORD);
            } else {
                requestPermission(Manifest.permission.RECORD_AUDIO, REQUEST_PERMISSION_RECORD);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permiso grabación ya otorgado!", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_WRITE:
                /*if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permiso escritura ya otorgado.", Toast.LENGTH_SHORT).show();
                    mIAdjuntar.setEnabled(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Permiso escritura denegado.", Toast.LENGTH_SHORT).show();
                    mIAdjuntar.setEnabled(true);
                }*/
                break;
            case REQUEST_PERMISSION_PHONE_READ:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permiso lectura ya otorgado.", Toast.LENGTH_SHORT).show();
                    //mIAdjuntar.setEnabled(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Permiso lectura denegado.", Toast.LENGTH_SHORT).show();
                    //mIAdjuntar.setEnabled(false);
                }
                break;
        }
    }

    private void showExplanation(String title,String message,final String permission,final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(Mensajeria.this,
                new String[]{permissionName}, permissionRequestCode);
    }
}
