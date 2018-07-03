package chat.atc.tges.tgeschat.databaseOnline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.security.GeneralSecurityException;

import chat.atc.tges.tgeschat.BaseActivity;
import chat.atc.tges.tgeschat.network.CustomSSLSocketFactory;

/**
 * Created by rodriguez on 10/10/2017.
 */

public class BaseVolleyActivity extends AppCompatActivity {

    private VolleyS volley;
    protected RequestQueue fRequestQueue;
    private CustomSSLSocketFactory oCustomSSLSocketFactory= new CustomSSLSocketFactory();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volley = VolleyS.getInstance(this);
        fRequestQueue = volley.getRequestQueue();
        /*try {
            fRequestQueue = Volley.newRequestQueue(this, new HurlStack(null,   CustomSSLSocketFactory.getSSLSocketFactory(this)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }*/
    }

    public void addToQueue(Request request) {
        if (request != null) {
            request.setTag(this);
            if (fRequestQueue == null){
                fRequestQueue = volley.getRequestQueue();
                /*try {
                    fRequestQueue = Volley.newRequestQueue(this, new HurlStack(null,   CustomSSLSocketFactory.getSSLSocketFactory(this)));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }*/
            }

            request.setRetryPolicy(new DefaultRetryPolicy(60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            onPreStartConnection();
            fRequestQueue.add(request);
        }
    }

    public void clearCache(){
        fRequestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                fRequestQueue.getCache().clear();
            }
        }
        );
    }

    public void onPreStartConnection() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void onConnectionFinished() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void onConnectionFailed(String error) {
        setProgressBarIndeterminateVisibility(true);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
