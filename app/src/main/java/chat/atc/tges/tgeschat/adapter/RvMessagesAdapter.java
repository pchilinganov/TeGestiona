package chat.atc.tges.tgeschat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import chat.atc.tges.tgeschat.BandejaActivity;
import chat.atc.tges.tgeschat.BandejaActivity222;
import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.dialogs.IncidenciaMasiva_dialog;
import chat.atc.tges.tgeschat.helper.CircleTransform;
import chat.atc.tges.tgeschat.helper.FlipAnimator;
import chat.atc.tges.tgeschat.model.Message;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;


public class RvMessagesAdapter extends RecyclerView.Adapter<RvMessagesAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private List<Message> messages;
    private List<Message> mFilteredMessagesList = new ArrayList<>();
    private List<Message> listaMessage= new ArrayList<>();
    private MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;
    String ticket="";
    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView from, subject, message, iconText, timestamp, idTicket;
        public ImageView iconImp, imgProfile, fondo_icono;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer, iconBack, iconFront,message_item;

        public MyViewHolder(View view) {
            super(view);
            idTicket = (TextView) view.findViewById(R.id.idTicket);
            from = (TextView) view.findViewById(R.id.from);
            subject = (TextView) view.findViewById(R.id.txt_primary);
            message = (TextView) view.findViewById(R.id.txt_secondary);
            iconText = (TextView) view.findViewById(R.id.icon_text);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            iconImp = (ImageView) view.findViewById(R.id.icon_star);
            fondo_icono = (ImageView) view.findViewById(R.id.icon_star);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            messageContainer = (LinearLayout) view.findViewById(R.id.message_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
            message_item = (RelativeLayout) view.findViewById(R.id.message_item);

            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public RvMessagesAdapter(Context mContext, List<Message> messages, MessageAdapterListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        mFilteredMessagesList = messages;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Message message = messages.get(position);

        listaMessage =messages;
        // displaying text view data
        //holder.idTicket.setText(message.getId());
        holder.from.setText(message.getFrom() + " (" + message.getMsgNoLeido() + ")");
        holder.subject.setText(message.getSubject());
        holder.message.setText(message.getMessage());
        holder.timestamp.setText(message.getTimestamp());

        // displaying the first letter of From in icon text
        holder.iconText.setText(message.getFrom().substring(0, 1));

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // change the font style depending on message read status
        applyReadStatus(holder, message);

        // handle message star
        applyImportant(holder, message);

        // handle icon animation
        //applyIconAnimation(holder, position);

        // display profile image
        applyProfilePicture(holder, message);

        // apply click events
        applyClickEvents(holder, position);

        if(messages.get(position).getEstadoEncuesta().equalsIgnoreCase("5")) // los tickets que tienen enuesta pendiente se colorean de amarillo
        {
            holder.message_item.setBackgroundColor(Color.YELLOW);
        }
        else
        {
            holder.message_item.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().trim().toLowerCase();

                if (charString.isEmpty() || charString.equalsIgnoreCase("") ) {
                    mFilteredMessagesList = varPublicas.listaMensajesbandeja;
                }
                else
                {
                    ArrayList<Message> filteredList = new ArrayList<>();
                    for (Message oMessage : varPublicas.listaMensajesbandeja) {
                        String ticket, tipo, motivo, fecha, nroConsulta;

                        ticket = String.valueOf(oMessage.getId()); //nro ticket
                        tipo = oMessage.getSubject(); //tipo
                        motivo = oMessage.getMessage(); //motivo
                        fecha = oMessage.getTimestamp(); //fecha
                        nroConsulta = oMessage.getNroConsulta(); //nroConsulta

                        if(ticket.toLowerCase().contains(charString) || tipo.toLowerCase().contains(charString) || nroConsulta.toLowerCase().contains(charString) || motivo.toLowerCase().contains(charString) || fecha.toLowerCase().contains(charString)) {
                            filteredList.add(oMessage);
                        }
                    }
                    mFilteredMessagesList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredMessagesList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredMessagesList = (ArrayList<Message>) filterResults.values;
                messages = (ArrayList<Message>) filterResults.values;
                notifyDataSetChanged();
                    /*messages.clear();
                    messages= mFilteredMessagesList;
                    notifyDataSetChanged();*/
            }
        };
    }

    private void applyClickEvents(final MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.iconImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconImportantClicked(position);
            }
        });

        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
                //Message message = messages.get(position)
                varPublicas.estadoHistorialTicket=1;
                varPublicas.chat_id= messages.get(position).getIdChat();
                Intent intent = new Intent(mContext, Mensajeria.class);
                intent.putExtra("key_idTicket",String.valueOf(messages.get(position).getId()));
                intent.putExtra("key_NroTickets",String.valueOf(messages.get(position).getId()));
                intent.putExtra("key_Estado",String.valueOf(messages.get(position).getEstado()));
                intent.putExtra("key_idChat",String.valueOf(messages.get(position).getIdChat()));
                //intent.putExtra("key_EstadoEncuesta",String.valueOf(messages.get(position).getEstadoEncuesta()));
                //intent.putExtra("key_NroTickets",String.valueOf(messages.get(position).getNroTickets()));
                mContext.startActivity(intent);

                /*Intent i = new Intent(Login.this,Mensajeria.class);
                i.putExtra("key_emisor",USER);
                startActivity(i);*/
            }
        });

        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                //mostrarDialogoIncidenciaMasiva();
                return true;
            }
        });
    }



    private void applyProfilePicture(MyViewHolder holder, Message message) {
        if (message.getEstado().equalsIgnoreCase("Abierto")) {

            Uri uriImagenTicketAbierto = Uri.parse("R.drawable.img_ticket_abierto");

                Glide.with(mContext).load(uriImagenTicketAbierto)
                        .thumbnail(0.5f)
                        .crossFade()
                        .transform(new CircleTransform(mContext))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imgProfile);
                //holder.imgProfile.setColorFilter(null);
                holder.iconText.setVisibility(View.GONE);

                holder.imgProfile.setImageResource(R.drawable.bg_circle_red);
                holder.fondo_icono.setImageResource(R.drawable.bg_circle_red);
                //holder.imgProfile.setColorFilter(message.getColor());
                holder.iconText.setVisibility(View.VISIBLE);
                holder.iconText.setText("A");
        }
        else if (message.getEstado().equalsIgnoreCase("Cerrado")) {
            Uri uriImagenTicketCerrado = Uri.parse("R.drawable.img_ticket_cerrado");

                Glide.with(mContext).load(uriImagenTicketCerrado)
                        .thumbnail(0.5f)
                        .crossFade()
                        .transform(new CircleTransform(mContext))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imgProfile);
                //holder.imgProfile.setColorFilter(null);
                holder.iconText.setVisibility(View.GONE);

                holder.imgProfile.setImageResource(R.drawable.bg_circle_blue);
                holder.fondo_icono.setImageResource(R.drawable.bg_circle_blue);
                //holder.imgProfile.setColorFilter(message.getColor());
                holder.iconText.setVisibility(View.VISIBLE);
                holder.iconText.setText("C");

        }
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }


    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    @Override
    public long getItemId(int position) {
        return messages.get(position).getId();
    }

    private void applyImportant(MyViewHolder holder, Message message) {
        if (message.isImportant()) {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_selected));
        } else {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_border_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_normal));
        }
    }

    private void applyReadStatus(MyViewHolder holder, Message message) {
        if (message.isRead()) {
            holder.from.setTypeface(null, Typeface.NORMAL);
            holder.subject.setTypeface(null, Typeface.NORMAL);
            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.message));
        } else {
            holder.from.setTypeface(null, Typeface.BOLD);
            holder.subject.setTypeface(null, Typeface.BOLD);
            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.from));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        messages.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface MessageAdapterListener {
        void onIconClicked(int position);

        void onIconImportantClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }
}