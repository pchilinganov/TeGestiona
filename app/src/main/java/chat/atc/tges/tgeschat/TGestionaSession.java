package chat.atc.tges.tgeschat;

import android.app.Application;
import java.util.Timer;
import java.util.TimerTask;
import chat.atc.tges.tgeschat.listeners.LogoutListener;

/**
 * Created by rodriguez on 04/01/2018.
 * Se controla tiempo de inactividad de usuario
 */

public class TGestionaSession extends Application {
    private LogoutListener listener;
    private Timer timer;

    public void startUserSession(){
        cancelTimer();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listener.onSessionLogout();
            }
        },3600000
        ); //1 hora de inactividad 3600000
    }

    private void cancelTimer()
    {
        if (timer != null)
            timer.cancel();
    }

    public void registerSessionListener(LogoutListener listener)
    {
        this.listener=listener;
    }

    public void onUserInteracted()
    {
        startUserSession();
    }
}
