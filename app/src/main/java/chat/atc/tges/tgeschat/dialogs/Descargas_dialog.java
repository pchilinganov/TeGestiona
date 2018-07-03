package chat.atc.tges.tgeschat.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.VolleyRP;
import chat.atc.tges.tgeschat.databaseOnline.DialogVolleyFragment;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

/**
 * Created by rodriguez on 20/06/2018.
 */

public class Descargas_dialog extends DialogVolleyFragment {

    TextView lblAnularPenalidad, lblCancelacionOrdenes, lblOrdenesSuspension, lblAnulacionPensalidadPlanSeguro,lblCartillaV2, lblGuiaRapidaUso, lblBaseErroresDelivery, lblTitle3;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_top_consultas_tiendas_dialog, null);

        lblAnularPenalidad = (TextView) v.findViewById(R.id.lblAnularPenalidad);
        lblCancelacionOrdenes = (TextView) v.findViewById(R.id.lblCancelacionOrdenes);
        lblOrdenesSuspension = (TextView) v.findViewById(R.id.lblOrdenesSuspension);
        lblAnulacionPensalidadPlanSeguro = (TextView) v.findViewById(R.id.lblAnulacionPenalidadSeguroPlanInteligente);
        lblCartillaV2  = (TextView) v.findViewById(R.id.lblCartillaV2);
        lblGuiaRapidaUso  = (TextView) v.findViewById(R.id.lblGuiaRapidaUso);
        lblBaseErroresDelivery = (TextView) v.findViewById(R.id.lblBaseErroresDelivery);
        lblTitle3 = (TextView) v.findViewById(R.id.lblTitle3);

        //Muestra la vista que contiene el diseño del dialog
        builder.setView(v);


        builder.setPositiveButton("VOLVER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        })
                /*.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })*/;

        //Anular Penalidad
        String text = "<a href='https://movistartayuda.com/web_chat/descargas/consulta-tiendas/anular-penalidad.docx'><font color='#2C6B9E'>"+ ">> Anular penalidad" +"</font></a>";
        lblAnularPenalidad.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblAnularPenalidad.setMovementMethod(LinkMovementMethod.getInstance());
        lblAnularPenalidad.setText(Html.fromHtml(text));


        //Anular Penalidad
        String text2 = "<a href='https://movistartayuda.com/web_chat/descargas/consulta-tiendas/cancelación-ordenes-registradas-canal.docx'>"+ ">> Cancelación de ordenes registradas en el mismo canal" +"</a>";
        lblCancelacionOrdenes.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblCancelacionOrdenes.setMovementMethod(LinkMovementMethod.getInstance());
        lblCancelacionOrdenes.setText(Html.fromHtml(text2));

        //Anular Penalidad
        String text3 = "<a href='https://movistartayuda.com/web_chat/descargas/consulta-tiendas/anular-penalidad.docx'>"+ ">> Órdenes de suspensión por perdida o robo" +"</a>";
        lblOrdenesSuspension.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblOrdenesSuspension.setMovementMethod(LinkMovementMethod.getInstance());
        lblOrdenesSuspension.setText(Html.fromHtml(text3));

        //Anular Penalidad
        String text4 = "<a href='https://movistartayuda.com/web_chat/descargas/consulta-tiendas/anular-penalidad.docx'>"+ ">> Anulación de penalidad-seguro y plan inteligente" +"</a>";
        lblAnulacionPensalidadPlanSeguro.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblAnulacionPensalidadPlanSeguro.setMovementMethod(LinkMovementMethod.getInstance());
        lblAnulacionPensalidadPlanSeguro.setText(Html.fromHtml(text4));

        //Cartilla V2
        String text5 = "<a href='https://movistartayuda.com/web_chat/descargas/cartilla-uso/cartilla-v2.pdf'>"+ ">> Cartilla V.2" +"</a>";
        lblCartillaV2.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblCartillaV2.setMovementMethod(LinkMovementMethod.getInstance());
        lblCartillaV2.setText(Html.fromHtml(text5));

        //Guia Rapida Uso
        String text6 = "<a href='https://movistartayuda.com/web_chat/descargas/cartilla-uso/video-instructivo.mp4'>"+ ">> Guía Rápida de Uso (Video)" +"</a>";
        lblGuiaRapidaUso.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblGuiaRapidaUso.setMovementMethod(LinkMovementMethod.getInstance());
        lblGuiaRapidaUso.setText(Html.fromHtml(text6));

        if (varPublicas.idCanal.equalsIgnoreCase("2841")){
            lblBaseErroresDelivery.setVisibility(View.VISIBLE);
            lblTitle3.setVisibility(View.VISIBLE);
            //Anular Penalidad
            String text7 = "<a href='https://movistartayuda.com/web_chat/descargas/consulta-tiendas/base-errores-tienda.xlsb'>"+ ">> Base de Errores Delivery 18/06/2018" +"</a>";
            lblBaseErroresDelivery.setClickable(true);
            //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
            lblBaseErroresDelivery.setMovementMethod(LinkMovementMethod.getInstance());
            lblBaseErroresDelivery.setText(Html.fromHtml(text7));
        }
        else {
            lblBaseErroresDelivery.setVisibility(View.GONE);
            lblTitle3.setVisibility(View.GONE);
        }
        return builder.create();
    }
}
