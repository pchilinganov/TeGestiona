package chat.atc.tges.tgeschat.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.databaseOnline.DialogVolleyFragment;

/**
 * Created by rodriguez on 20/06/2018.
 */

public class GuiasUso_dialog extends DialogVolleyFragment {

    TextView lblCartillaV2, lblGuiaRapidaUso;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createTopConsultasTiendasDialog();
    }

    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de activación
     *
     * @return Diálogo
     */

    public AlertDialog createTopConsultasTiendasDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_top_consultas_tiendas_dialog, null);

        lblCartillaV2 = (TextView) v.findViewById(R.id.lblAnularPenalidad);
        lblGuiaRapidaUso = (TextView) v.findViewById(R.id.lblCancelacionOrdenes);

        //Muestra la vista que contiene el diseño del dialog
        builder.setView(v);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        })
                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });



        return builder.create();
    }
}
