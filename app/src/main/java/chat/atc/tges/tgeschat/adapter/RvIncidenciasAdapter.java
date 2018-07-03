package chat.atc.tges.tgeschat.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.model.Incidencia;

public class RvIncidenciasAdapter extends RecyclerView.Adapter<RvIncidenciasAdapter.PaletteViewHolder> {
    private List<Incidencia> incidencias;
    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

    public RvIncidenciasAdapter(List<Incidencia> incidencias) {
        this.incidencias = incidencias;
        //this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @Override
    public PaletteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_incidencia, parent, false);
        return new PaletteViewHolder(row);
    }

    @Override
    public void onBindViewHolder(PaletteViewHolder holder, int position) {
        String motivo="", cuerpo1="",cuerpo2="", cuerpo3="", ticketDoit="";
        cuerpo1 = incidencias.get(position).getCuerpo1();
        cuerpo2 = incidencias.get(position).getCuerpo2();
        cuerpo3 = incidencias.get(position).getCuerpo3();
        motivo = incidencias.get(position).getMotivo();
        ticketDoit = incidencias.get(position).getTicketDoit();

        String text = "<p>" + cuerpo1 + " <strong>" + motivo.toUpperCase() + "</strong>" + cuerpo2 + " <strong>" + ticketDoit + "</strong>" + cuerpo3 +"</p>";
        //holder.TvMensaje.setText(Html.fromHtml(text));

        //holder.getTitleTextView().setText(cuerpo1 + motivo + cuerpo2 + ticketDoit + cuerpo3) ;

        //holder.getTxtCuerpo1().setText(cuerpo1 + Html.fromHtml(text) + cuerpo2 + ticketDoit + cuerpo3) ;
        holder.getTxtCuerpo1().setText(Html.fromHtml(text)) ;
        /*holder.getTxtCuerpo2().setText(cuerpo2) ;
        holder.getTxtMotivo().setText(motivo) ;
        holder.getTxtTicketDoit().setText(ticketDoit) ;
        holder.getTxtCuerpo3().setText(cuerpo3) ;*/

        // holder.getTitleTextView().setText(incidencias.get(position).toString() + "que fue 2") ;
        // holder.getSubtitleTextView().setText(color.getHex());

        /*GradientDrawable gradientDrawable = (GradientDrawable) holder.getCircleView().getBackground();
        int colorId = android.graphics.Color.parseColor(color.());
        gradientDrawable.setColor(colorId);*/
    }

    @Override
    public int getItemCount() {
        return incidencias.size();
    }



    class PaletteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtCuerpo1, txtCuerpo2, txtMotivo, txtTicketDoit, txtCuerpo3;

        public PaletteViewHolder(View itemView) {
            super(itemView);
            txtCuerpo1 = (TextView) itemView.findViewById(R.id.txtCuerpo1);
            /*txtCuerpo2 = (TextView) itemView.findViewById(R.id.txtCuerpo2);
            txtMotivo = (TextView) itemView.findViewById(R.id.txtMotivo);
            txtTicketDoit = (TextView) itemView.findViewById(R.id.txtTicketDoit);
            txtCuerpo3 = (TextView) itemView.findViewById(R.id.txtCuerpo3);*/

            itemView.setOnClickListener(this);
        }

        public TextView getTxtCuerpo1() {
            return txtCuerpo1;
        }

        /*public TextView getTxtCuerpo2() {
            return txtCuerpo2;
        }

        public TextView getTxtMotivo() {
            return txtMotivo;
        }

        public TextView getTxtTicketDoit() {
            return txtTicketDoit;
        }

        public TextView getTxtCuerpo3() {
            return txtCuerpo3;
        }*/

        @Override
        public void onClick(View v) {
            //recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());
        }
    }

}
