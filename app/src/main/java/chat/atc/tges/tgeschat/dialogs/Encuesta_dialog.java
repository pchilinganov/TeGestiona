package chat.atc.tges.tgeschat.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.databaseOnline.DialogVolleyFragment;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

import static chat.atc.tges.tgeschat.varPublicas.varPublicas.chat_id;

/**
 * Created by rodriguez on 30/04/2018.
 */

public class Encuesta_dialog extends DialogVolleyFragment implements OnClickListener {
    ImageButton btnMuyBueno, btnBueno, btnMalo, btnRegular;


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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_encuesta_dialog, null);

        btnMuyBueno = (ImageButton) v.findViewById(R.id.btnMuyBueno);
        btnMuyBueno.setOnClickListener(this);
        btnBueno = (ImageButton) v.findViewById(R.id.btnBueno);
        btnBueno.setOnClickListener(this);
        btnRegular = (ImageButton) v.findViewById(R.id.btnRegular);
        btnRegular.setOnClickListener(this);
        btnMalo = (ImageButton) v.findViewById(R.id.btnMalo);
        btnMalo.setOnClickListener(this);
        //chat_id=0;
        //Muestra la vista que contiene el diseño del dialog
        builder.setView(v);

        if (chat_id <=0){
            Toast.makeText(getActivity(), "No está asociado un id de chat para realizar la encuesta.", Toast.LENGTH_LONG).show();
            dismiss();
        }

        /*
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        }
        ); */

        //listarIncidencias();

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

    private void makeRequestEncuesta() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = varPublicas.URL_DESARROLLO+"recibirDataEncuesta";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                getDataEncuesta(response);
                //Toast centrado
                Toast toast = Toast.makeText(getActivity(), WS_Encuesta_Msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

                //Abre una nueva ventana de chat para generar otro ticket
                chat_id=0;
                Intent intent = new Intent(getActivity(), Mensajeria.class);
                startActivity(intent);
            }
        }   , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("evaluacion", evaluacion);
                params.put("idchat", ""+ chat_id);
                params.put("token", varPublicas.tokenMovil);
                params.put("idvendedor",varPublicas.idVendedorChat);

                return params;
            }};
        queue.add(request);
    }

    String WS_Encuesta_Msg ="";
    boolean WS_Encuesta_Estado =false;

    public void getDataEncuesta(String cadenaJSON){
        try{
            JSONArray jsonArrayData=new JSONArray(cadenaJSON);
            WS_Encuesta_Msg = jsonArrayData.getJSONObject(0).getString("Msg").toString();
            WS_Encuesta_Estado = Boolean.parseBoolean(jsonArrayData.getJSONObject(0).getString("Estado").toString());
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }

    String evaluacion="";
    @Override
    public void onClick(View v) {
        if (v==btnMalo){
            evaluacion ="4";
            Toast.makeText(getActivity(), "Gracias por Participar!.", Toast.LENGTH_LONG);
        } else if (v== btnRegular){
            evaluacion ="3";
            Toast.makeText(getActivity(), "Gracias por Participar!.", Toast.LENGTH_LONG);
        } else if (v== btnBueno){
            evaluacion ="2";
            Toast.makeText(getActivity(), "Gracias por Participar!.", Toast.LENGTH_LONG);
        } else if (v== btnMuyBueno){
            evaluacion ="1";
            Toast.makeText(getActivity(), "Gracias por Participar!.", Toast.LENGTH_LONG);
        }
        makeRequestEncuesta();
    }
}
