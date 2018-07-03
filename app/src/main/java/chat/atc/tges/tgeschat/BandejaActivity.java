package chat.atc.tges.tgeschat;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Filter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.adapter.RvMessagesAdapter;
import chat.atc.tges.tgeschat.dialogs.IncidenciaMasiva_dialog;
import chat.atc.tges.tgeschat.model.Message;
import chat.atc.tges.tgeschat.helper.DividerItemDecoration;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.listaMensajesbandeja;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.usuario;

public class BandejaActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, RvMessagesAdapter.MessageAdapterListener {
    public static List<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private RvMessagesAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback  actionModeCallback;
    private ActionMode actionMode;
    private VolleyRP volley;
    private RequestQueue mRequest;
    public static FragmentManager fragmentManager;
    android.support.v4.app.FragmentManager supportFragmentManager =  getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
    public static FragmentTabHost tabHost; //manejará pestañas fragment

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandeja);
        getSupportActionBar().setTitle("Bandeja de tickets");

        fragmentManager= getFragmentManager();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        /*
        //Click en fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new RvMessagesAdapter(this, messages, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        actionModeCallback = new ActionModeCallback();

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getInbox();
                    }
                }
        );
        //se invoca al método para que aparezcan los gráficos coloreados
        getInbox();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);

        RecibirHistorialTicket();
        mAdapter.notifyDataSetChanged();
    }

    private void RecibirHistorialTicket(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "listarTicket", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // clear the inbox
                messages.clear();
                getDataHistorial(response);

                //Toast.makeText(Mensajeria.this,response, Toast.LENGTH_SHORT).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BandejaActivity.this,"Respuesta Bandeja Activity: " + error.toString() , Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", varPublicas.tokenMovil);
                params.put("idvendedor",varPublicas.idVendedorChat);

                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }

    String  WS_MsgHistorial_Msg="";
    Boolean WS_MsgHistorial_Estado=false;

    public void getDataHistorial (String cadenaJSON){
        try{
            varPublicas.listaMensajesbandeja.clear();
            JSONArray jsonArrayDataHistorial=new JSONArray(cadenaJSON);
            WS_MsgHistorial_Msg= jsonArrayDataHistorial.getJSONObject(0).getString("Msg").toString();
            WS_MsgHistorial_Estado= jsonArrayDataHistorial.getJSONObject(0).getBoolean("Estado");

            JSONArray jsonArrayListaDatos = jsonArrayDataHistorial.getJSONObject(0).getJSONArray("ListaDatos");
            String from="", subject="",message="", timestamp="",picture="", estado="",msg_noLeido, estadoEncuesta="", nroConsulta="";
            int idTicket=0, idChat=0, idPropietario=0, tipoMensaje=0;

            for (int i = 0; i < jsonArrayListaDatos.length(); i++) {
                idTicket= jsonArrayListaDatos.getJSONObject(i).getInt("numero_ticket");
                from= "Ticket #" + jsonArrayListaDatos.getJSONObject(i).getString("numero_ticket").toString();
                subject= jsonArrayListaDatos.getJSONObject(i).getString("tipo").toString();
                message= jsonArrayListaDatos.getJSONObject(i).getString("motivo").toString();
                timestamp=jsonArrayListaDatos.getJSONObject(i).getString("fecha").toString();
                estado=jsonArrayListaDatos.getJSONObject(i).getString("estado").toString();
                idChat =jsonArrayListaDatos.getJSONObject(i).getInt("idchat");
                msg_noLeido = jsonArrayListaDatos.getJSONObject(i).getString("mmsg_no_leido").toString();
                nroConsulta = jsonArrayListaDatos.getJSONObject(i).getString("telefono").toString();

                CreateTicket(idTicket, idChat, from.trim(), subject.trim(),message.trim(), timestamp.trim(),"",false,false,msg_noLeido, estado.trim(), estadoEncuesta, nroConsulta);//estado.trim()
            }

            mAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

        }
        catch(JSONException je) {
            je.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    //añadir idChat
    private void CreateTicket(int  idTicket, int idChat, String from, String subject, String message, String timestamp, String picture, boolean isImportant, boolean isRead, String msgNoLeido, String estado, String estadoEncuesta, String nroConsulta){//String estado
        Message mensaje = new Message();
        mensaje.setId(idTicket);
        mensaje.setIdChat(idChat);
        mensaje.setFrom(from);
        mensaje.setSubject(subject);
        mensaje.setMessage(message);
        mensaje.setTimestamp(timestamp);
        mensaje.setPicture(picture);
        mensaje.setImportant(false);
        mensaje.setRead(false);
        mensaje.setEstado(estado);
        mensaje.setMsgNoLeido(msgNoLeido);
        mensaje.setEstadoEncuesta(estadoEncuesta);
        mensaje.setNroConsulta(nroConsulta);
        listaMensajesbandeja.add(mensaje);
        messages.add(mensaje);
        mAdapter.notifyDataSetChanged();

    }

    /*private String from;
    private String subject;
    private String message;
    private String timestamp;
    private String picture;
    private boolean isImportant;
    private boolean isRead;
    private int color = -1;*/

    /*public void CreateMensaje(String mensaje, String hora, int tipoDeMensaje, String nombre){
        MensajeDeTexto mensajeDeTextoAuxiliar = new MensajeDeTexto();
        mensajeDeTextoAuxiliar.setId("0");
        mensajeDeTextoAuxiliar.setNombre(nombre);
        mensajeDeTextoAuxiliar.setMensaje(mensaje);
        mensajeDeTextoAuxiliar.setTipoMensaje(tipoDeMensaje);
        mensajeDeTextoAuxiliar.setHoraDelMensaje(hora);
        mensajeDeTextos.add(mensajeDeTextoAuxiliar);
        adapter.notifyDataSetChanged();
        setScrollbarChat(); //mantiene el chat en el último mensaje enviado o recibido
    }*/

    /**
     * Fetches mail messages by making HTTP request
     * url: http://api.androidhive.info/json/inbox.json
     */

    /*private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<List<Message>> call = apiService.getInbox();
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                // clear the inbox
                messages.clear();

                // add all the messages
                // messages.addAll(response.body());

                // TODO - avoid looping
                // the loop was performed to add colors to each message
                for (Message message : response.body()) {
                    // generate a random color
                    message.setColor(getRandomMaterialColor("400"));
                    messages.add(message);
                }

                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }*/

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_bandeja, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //permite modificar el hint que el EditText muestra por defecto
        //searchView.setQueryHint(getText(R.string.search));
        //searchView.setOnQueryTextListener(this);
        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter != null) mAdapter.getFilter().filter(newText);
                return true;
            }
        }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //Toast.makeText(getApplicationContext(), "Buscar...", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_Notificaciones) {
            mostrarDialogoIncidenciaMasiva();
            return true;
        }

        if (id == R.id.action_CerrarSesion) {
            logout();
            Intent intent = new Intent(BandejaActivity.this, LoginTelefonica.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String estado="";
                estado="OK";
                //Toast.makeText(BandejaActivity.this,response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BandejaActivity.this,"Respuesta mensajeria: " + error.toString() , Toast.LENGTH_SHORT).show();
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

    //Muestra Dialogo de Búsqueda de Expediente Proactivo
    private void mostrarDialogoIncidenciaMasiva()
    {
        IncidenciaMasiva_dialog myDiag = new IncidenciaMasiva_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "IncidenciaMasiva_dialog");
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        getInbox();
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    @Override
    public void onIconImportantClicked(int position) {
        // Star icon is clicked,
        // mark the message as important
        Message message = messages.get(position);
        message.setImportant(!message.isImportant());
        messages.set(position, message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
            Message message = messages.get(position);
            message.setRead(true);
            messages.set(position, message);
            mAdapter.notifyDataSetChanged();

            //Toast.makeText(getApplicationContext(), "Read: " + message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // delete all the selected messages
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteMessages() {
        mAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }
}
