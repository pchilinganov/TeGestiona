package chat.atc.tges.tgeschat.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
import chat.atc.tges.tgeschat.Mensajes.DividerItemDecoration;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.VolleyRP;
import chat.atc.tges.tgeschat.adapter.RvIncidenciasAdapter;
import chat.atc.tges.tgeschat.databaseOnline.DialogVolleyFragment;
import chat.atc.tges.tgeschat.model.Incidencia;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;

/**
 * Created by rodriguez on 30/04/2018.
 */

public class IncidenciaMasiva_dialog extends DialogVolleyFragment { //implements SwipeRefreshLayout.OnRefreshListener

    List<Incidencia> incidencias = new ArrayList<>();
    RvIncidenciasAdapter IAdapter;
    RecyclerView recyclerView;
    private VolleyRP volley;
    private RequestQueue mRequest;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createIncidenciaMasivaDialog();
    }

    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de activación
     *
     * @return Diálogo
     */

    public AlertDialog createIncidenciaMasivaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_incidencia_masiva, null);


        volley = VolleyRP.getInstance(getActivity());
        mRequest = volley.getRequestQueue();
        IAdapter = new RvIncidenciasAdapter(incidencias);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(IAdapter);
        //VERTICAL
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //HORIZONTAL
        //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        //swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        //swipeRefreshLayout.setOnRefreshListener(this);

        /*Incidencia oIncidencia = new Incidencia();
        oIncidencia.setCuerpo1("Se ha presentado una incidencia masiva debido a ");
        oIncidencia.setMotivo("caída de red");
        oIncidencia.setCuerpo2(", el caso ha sido reportado a Segundo Nivel a través de ticket Doit ");
        oIncidencia.setTicketDoit("1234567");
        oIncidencia.setCuerpo3(". Si cuenta con este problema favor no generar un chat pues ya fue reportado. Se estará informando la solución por este medio.");

        incidencias.add(oIncidencia);
        IAdapter.notifyDataSetChanged();*/

        //Muestra la vista que contiene el diseño del dialog
        builder.setView(v);

        builder.setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        listarIncidencias();

        /*swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        listarIncidencias();
                    }
                }
        );*/
        return builder.create();
    }

    private void listarIncidencias(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "listarIncidencias", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // clear the rv
                incidencias.clear();
                getDataIncidencias(response);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error Dialogo Incidencia Masiva: " + error.toString() , Toast.LENGTH_SHORT).show();
                //swipeRefreshLayout.setRefreshing(false);
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
        VolleyRP.addToQueue(request,mRequest,getActivity(),volley);
    }

    String  WS_MsgIncidencia_Msg="";
    Boolean WS_MsgIncidencia_Estado=false;

    public void getDataIncidencias (String cadenaJSON){
        try
        {
            JSONArray jsonArrayDataIncidencias=new JSONArray(cadenaJSON);
            WS_MsgIncidencia_Msg= jsonArrayDataIncidencias.getJSONObject(0).getString("Msg").toString();
            WS_MsgIncidencia_Estado= jsonArrayDataIncidencias.getJSONObject(0).getBoolean("Estado");

            if (jsonArrayDataIncidencias.getJSONObject(0).getJSONArray("ListaDatos")!=null){
                String a="";
                a="es un array";
            }else{
                String b="";
                b="soy un string";
            }

            JSONArray jsonArrayListaDatos = jsonArrayDataIncidencias.getJSONObject(0).getJSONArray("ListaDatos");
            Incidencia oIncidencia;
            for (int i = 0; i < jsonArrayListaDatos.length(); i++) {
                oIncidencia = new Incidencia();
                oIncidencia.setMotivo(jsonArrayListaDatos.getJSONObject(i).getString("motivo").toString());
                oIncidencia.setCuerpo1(jsonArrayListaDatos.getJSONObject(i).getString("cuerpo1").toString());
                oIncidencia.setCuerpo2(jsonArrayListaDatos.getJSONObject(i).getString("cuerpo2").toString());
                oIncidencia.setCuerpo3(jsonArrayListaDatos.getJSONObject(i).getString("cuerpo3").toString());
                oIncidencia.setTicketDoit(jsonArrayListaDatos.getJSONObject(i).getString("ticket_doit").toString());
                incidencias.add(oIncidencia);
                IAdapter.notifyDataSetChanged();
            }

            //IAdapter.notifyDataSetChanged();
            //swipeRefreshLayout.setRefreshing(false);
        }
        catch(JSONException je) {
            je.printStackTrace();
            //swipeRefreshLayout.setRefreshing(false);
        }
    }
/*
    @Override
    public void onRefresh() {
        listarIncidencias();
    }*/
}
