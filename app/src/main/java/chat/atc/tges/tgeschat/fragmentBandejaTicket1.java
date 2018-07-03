package chat.atc.tges.tgeschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.atc.tges.tgeschat.adapter.RvMessagesAdapter;
import chat.atc.tges.tgeschat.helper.DividerItemDecoration;
import chat.atc.tges.tgeschat.model.Message;
import chat.atc.tges.tgeschat.varPublicas.GlobalFunctions;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.listaMensajesbandeja;

/**
 * Created by rodriguez on 06/06/2018.
 */

public class fragmentBandejaTicket1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RvMessagesAdapter.MessageAdapterListener{

    public static List<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    public static RvMessagesAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    public static VolleyRP volley;
    public static RequestQueue mRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_bandeja_ticket_one_fragment, container, false);

        volley = VolleyRP.getInstance(getActivity());
        mRequest = volley.getRequestQueue();

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new RvMessagesAdapter(getContext(), messages, fragmentBandejaTicket1.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);


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


        return v;
    }

    @Override
    public void onRefresh() {
        getInbox();
    }

    @Override
    public void onResume() {
        super.onResume();

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

    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);

        RecibirHistorialTicket();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onIconClicked(int position) {
        /*if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);*/
    }

    @Override
    public void onIconImportantClicked(int position) {
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
            //enableActionMode(position);
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
            //enableActionMode(position);
    }

    private void RecibirHistorialTicket(){


        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "listarTicket", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // clear the inbox
                messages.clear();
                getDataHistorial(response);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Respuesta Bandeja Activity: " + error.toString() , Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("token", varPublicas.tokenMovil);
                params.put("token", varPublicas.tokenMovil);
                params.put("idvendedor",varPublicas.idVendedorChat);

                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,getContext(),volley);
    }

    String  WS_MsgHistorial_Msg="";
    Boolean WS_MsgHistorial_Estado=false;
    Boolean WS_Session_Msg=false;

    public void getDataHistorial (String cadenaJSON){
        try{
            varPublicas.listaMensajesbandeja.clear();
            JSONArray jsonArrayDataHistorial=new JSONArray(cadenaJSON);
            WS_MsgHistorial_Msg= jsonArrayDataHistorial.getJSONObject(0).getString("Msg").toString();
            WS_MsgHistorial_Estado= jsonArrayDataHistorial.getJSONObject(0).getBoolean("Estado");
            WS_Session_Msg= jsonArrayDataHistorial.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

            if (WS_Session_Msg!=null && !WS_Session_Msg)
            {
               GlobalFunctions.validaSesion(WS_Session_Msg);
               Intent intent = new Intent(getActivity(), LoginTelefonica.class);
               startActivity(intent);
               Toast.makeText(getContext(), "Sesión finalizada.", Toast.LENGTH_SHORT).show();
               return;
            }

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
                estadoEncuesta = jsonArrayListaDatos.getJSONObject(i).getString("encuesta").toString();

                CreateTicket(idTicket, idChat, from.trim(), subject.trim(),message.trim(), timestamp.trim(),"",false,false,msg_noLeido, estado.trim(), estadoEncuesta, nroConsulta);//estado.trim()
            }
            mAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
        catch(JSONException je)
        {
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
