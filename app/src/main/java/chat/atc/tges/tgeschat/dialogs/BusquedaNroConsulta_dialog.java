package chat.atc.tges.tgeschat.dialogs;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.volley.RequestQueue;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.VolleyRP;

/**
 * Created by rodriguez on 06/06/2018.
 */

public class BusquedaNroConsulta_dialog extends DialogFragment implements OnClickListener {

    private VolleyRP volley;
    private RequestQueue mRequest;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createBusquedaNroConsultaDialog();
    }

    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de activación
     *
     * @return Diálogo
     */

    public AlertDialog createBusquedaNroConsultaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_busqueda_nro_consulta, null);

        /*volley = VolleyRP.getInstance(getActivity());
        mRequest = volley.getRequestQueue();*/

        //Muestra la vista que contiene el diseño del dialog
        builder.setView(v);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

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


    @Override
    public void onClick(View view) {

    }
}
