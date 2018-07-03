package chat.atc.tges.tgeschat.Services;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Random;
import chat.atc.tges.tgeschat.LoginTelefonica;
import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;


public class FireBaseServiceMensajes extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        int tipo = Integer.parseInt(remoteMessage.getData().get("tipo"));

        boolean accionIncidencia=false;

        String titulo, body;
        int subtipo=0;

            switch (tipo){
                case 1 : // Restablecer contraseña | Push que avisa cuando ususario debe revisar su correo
                    titulo = remoteMessage.getNotification().getTitle();
                    body = remoteMessage.getNotification().getBody();
                    showNotificationNoAction(titulo, body); // no tenga ida a otras actividades
                    break;
                case 2: //Mensajes de chat
                    subtipo = Integer.parseInt(remoteMessage.getData().get("subtipo"));

                    if (subtipo==1)
                    {
                        String mensaje = remoteMessage.getData().get("mensaje");
                        String hora = remoteMessage.getData().get("hora");
                        String cabezera = remoteMessage.getData().get("cabecera");
                        String cuerpo = remoteMessage.getData().get("cuerpo");
                        String agente = remoteMessage.getData().get("agente");
                        String tipoMsg = remoteMessage.getData().get("tipo_msg");
                        String urlArchivo="";
                        if (tipoMsg.equalsIgnoreCase("file")) {
                            urlArchivo = remoteMessage.getData().get("url");
                        }
                        if (agente !=null) {
                            varPublicas.agenteMesaAyuda = agente;
                        }
                        String idChat = remoteMessage.getData().get("idchat");
                        String idVendedor = remoteMessage.getData().get("idvendedor");
                        String idTicket = remoteMessage.getData().get("idticket");
                        MensajeChatBroadCast(mensaje, hora, Integer.parseInt(idChat), tipoMsg, urlArchivo,idTicket);
                        //showNotificationNoAction(titulo, body);
                        showNotificationChatMsg(cabezera, cuerpo, idChat, idVendedor, idTicket);
                    }
                    else if (subtipo==2)
                    {
                        String cabecera = remoteMessage.getData().get("cabecera");
                        String cuerpo = remoteMessage.getData().get("cuerpo");
                        showNotificationNoAction(cabecera, cuerpo);
                    }
                    break;

                case 3 : // Push Incidencia masiva
                    subtipo = Integer.parseInt(remoteMessage.getData().get("subtipo"));
                    String estado="";
                    //titulo = remoteMessage.getNotification().getTitle();
                    //body = remoteMessage.getNotification().getBody();

                    if (subtipo == 1)
                    { //subtipo 1 -> Usuario no logueado, push no manda a otras actividades
                        //showNotificationNoAction(titulo, body); // no tenga ida a otras actividades
                        InciMasivaBroadCast("OK");
                    }
                    else if (subtipo == 2)
                    { //avisa la solución de la incidencia masiva
                        titulo = remoteMessage.getNotification().getTitle();
                        String cabecera="", cuerpo="", cuerpo1="", cuerpo2="", motivo="", ticket_doit="", hora="";
                        cabecera = remoteMessage.getData().get("cabecera");
                        cuerpo = remoteMessage.getData().get("cuerpo");
                        cuerpo1 = remoteMessage.getData().get("cuerpo1");
                        cuerpo2 = remoteMessage.getData().get("cuerpo2");
                        motivo = remoteMessage.getData().get("motivo");
                        ticket_doit = remoteMessage.getData().get("ticket_doit");
                        motivo = remoteMessage.getData().get("motivo");

                        String bodyResolucion="";
                        bodyResolucion= cuerpo1 + motivo + cuerpo2  + ticket_doit ;
                        showNotificationNoAction(cabecera, bodyResolucion);
                    }
                    break;
                case 4: // Push de Encuesta
                    subtipo = Integer.parseInt(remoteMessage.getData().get("subtipo"));
                    if (subtipo==1)
                    {
                        String idChat = remoteMessage.getData().get("idchat");
                        AvisoEncuesta("OK", idChat);
                    }
                    break;
                case 6: // Push de Actualización de App
                    //subtipo = Integer.parseInt(remoteMessage.getData().get("subtipo"));
                    String cabecera, cuerpo, url;
                    cabecera = remoteMessage.getData().get("cabecera");
                    cuerpo = remoteMessage.getData().get("cuerpo");
                    url = remoteMessage.getData().get("url");
                    showNotificationWithActionPlayStore(cabecera,cuerpo,url);
                    break;
            }
    }

    private void AvisoEncuesta(String estado, String idChat)
    {
        Intent i = new Intent(Mensajeria.MENSAJE);
        i.putExtra("key_encuesta",estado);
        i.putExtra("key_idChat", String.valueOf(idChat));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }

    private void MensajeChatBroadCast(String mensaje, String hora, int idChat, String tipoMsg, String urlArchivo, String idTicket)
    {
        Intent i = new Intent(Mensajeria.MENSAJE);
        i.putExtra("key_mensaje",mensaje);
        i.putExtra("key_hora",hora);
        i.putExtra("key_idChat", String.valueOf(idChat));
        i.putExtra("key_tipoMsg", tipoMsg);
        i.putExtra("key_urlArchivo", urlArchivo);
        i.putExtra("key_idTicket", idTicket);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }

    private void InciMasivaBroadCast(String estado)
    {
        LoginTelefonica.existeIncidenciaMasiva=true;
        Intent i = new Intent(Mensajeria.MENSAJE);
        i.putExtra("key_IM",estado);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }

    private void showNotificationChatMsg(String cabezera, String cuerpo, String idChat, String idVendedor, String idTicket)
    {
        int mNotificationId=Integer.parseInt(idTicket);
        String channelId="my_channel_01";
        NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(this,null);

        NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(this,null);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            // El nombre visible del canal
            CharSequence name = "Mensajes";

            //Descripción del canal
            String descripcion="MensajeChatBroadCast entre agente y vendedor";
            int importancia= NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel= new NotificationChannel(channelId,name,importancia);

            //Configuración de canal de notificación
            mChannel.setDescription(descripcion);
            mChannel.enableLights(true);

            //Establece el color de luz de notificación del canal , si es que el dispositivo lo soporta
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[] {100,200,300,400,500,400,300,200,400});
            notificationManager.createNotificationChannel(mChannel);

            mBuilder = new NotificationCompat.Builder(this,channelId);
        }

        Intent i = new Intent(this,Mensajeria.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (idTicket!=null || idChat!=null || idVendedor!=null ) {
            if (!idTicket.isEmpty() || !idChat.isEmpty() || !idVendedor.isEmpty()) {
                i.putExtra("key_idTicket", idTicket);
                i.putExtra("key_idChat", idChat);
                i.putExtra("key_idVendedor", idVendedor);
                varPublicas.estadoHistorialTicket = 1;
            }
        }

        String GROUP_KEY_CHAT_MSG = "chat.atc.tges.tgeschat";
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT); //FLAG_UPDATE_CURRENT //FLAG_ONE_SHOT

        Uri soundNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Grupo de Notificaciones
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle(cabezera);
        mBuilder.setContentText(cuerpo);
        mBuilder.setSound(soundNotification);
        mBuilder.setSmallIcon(R.mipmap.logo_mesatayuda);
        inboxStyle.setBigContentTitle("MovistarTAyuda");
        mBuilder.setTicker(cuerpo);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setGroup(GROUP_KEY_CHAT_MSG);

        //builder.setStyle(inboxStyle);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(cuerpo)); //Esta línea permite que mensaje push se muestre en varias línes en el dispositivo



        Random random = new Random();

        //notificationManager.notify(random.nextInt(),builder.build());
        notificationManager.notify(mNotificationId,mBuilder.build());

        //Envia a Mensajeria.class para que muestre historail de de idChat
        //varPublicas.estadoHistorialTicket=1;
    }

    private void showNotificationNoAction(String cabezera, String body){

        int mNotificationId=0;
        String channelId="my_channel_02";
        NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(this,null);

        NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(this,null);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            // El nombre visible del canal
            CharSequence name = "Incidencias Masivas";

            //Descripción del canal
            String descripcion="Incidencias de carácter masivo";
            int importancia= NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel= new NotificationChannel(channelId,name,importancia);

            //Configuración de canal de notificación
            mChannel.setDescription(descripcion);
            mChannel.enableLights(true);

            //Establece el color de luz de notificación del canal , si es que el dispositivo lo soporta
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[] {100,200,300,400,500,400,300,200,400});
            notificationManager.createNotificationChannel(mChannel);

            mBuilder = new NotificationCompat.Builder(this,channelId);
        }

        Uri soundNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle(cabezera);
        mBuilder.setContentText(body);
        mBuilder.setSound(soundNotification);
        mBuilder.setSmallIcon(R.mipmap.logo_mesatayuda); //cambiar por el de colores
        mBuilder.setTicker(body);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        //builder.setContentIntent(pendingIntent);

        Random random = new Random();
        notificationManager.notify(mNotificationId,mBuilder.build());
    }

    private void showNotificationWithActionIncidenciaMasiva(String cabezera, String body){

        Intent i = new Intent(this,Mensajeria.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_ONE_SHOT);
        Uri soundNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Builder builder = new Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle(cabezera);
        builder.setContentText(body);
        builder.setSound(soundNotification);
        builder.setSmallIcon(R.mipmap.logo_mesatayuda);
        builder.setTicker(body);
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();

        notificationManager.notify(random.nextInt(),builder.build());
    }

    private void showNotificationWithAction(String cabezera, String body){

        Intent i = new Intent(this, LoginTelefonica.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_ONE_SHOT);

        Uri soundNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Builder builder = new Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle(cabezera);
        builder.setContentText(body);
        builder.setSound(soundNotification);
        builder.setSmallIcon(R.mipmap.logo_mesatayuda);
        builder.setTicker(body);
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();

        notificationManager.notify(random.nextInt(),builder.build());

    }

    private void showNotificationWithActionPlayStore(String cabezera, String body, String url){

        int mNotificationId=6;
        //
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,myIntent, PendingIntent.FLAG_ONE_SHOT);

            Uri soundNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Builder builder = new Builder(this);
            builder.setAutoCancel(true);
            builder.setContentTitle(cabezera);
            builder.setContentText(body);
            builder.setSound(soundNotification);
            builder.setSmallIcon(R.mipmap.logo_mesatayuda);
            builder.setTicker(body);
            builder.setContentIntent(pendingIntent);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Random random = new Random();

            notificationManager.notify(mNotificationId,builder.build());

//            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }



        // -----



    }
    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
