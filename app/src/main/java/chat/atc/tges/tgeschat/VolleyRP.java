package chat.atc.tges.tgeschat;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.security.GeneralSecurityException;

import chat.atc.tges.tgeschat.network.CustomSSLSocketFactory;

public class VolleyRP {
    
    private static VolleyRP mVolleyRP = null;
    private RequestQueue mRequestQueue;
    private CustomSSLSocketFactory oCustomSSLSocketFactory= new CustomSSLSocketFactory();

    private VolleyRP(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        /*try {
            mRequestQueue = Volley.newRequestQueue(context, new HurlStack(null,   CustomSSLSocketFactory.getSSLSocketFactory(context)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }*/
    }

    public static VolleyRP getInstance(Context context) {
        if (mVolleyRP == null) {
            mVolleyRP = new VolleyRP(context);

        }
        return mVolleyRP;
    }


    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public static void addToQueue(Request request, RequestQueue fRequestQueue, Context context, VolleyRP volley) {
        if (request != null) {
            request.setTag(context);
            if (fRequestQueue == null)
                fRequestQueue = volley.getRequestQueue();
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            fRequestQueue.add(request);
        }
    }
}
