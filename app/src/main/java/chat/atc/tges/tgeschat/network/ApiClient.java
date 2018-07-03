package chat.atc.tges.tgeschat.network;

import chat.atc.tges.tgeschat.varPublicas.varPublicas;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL =  "http://api.androidhive.info/json/";
    //public static final String BASE_URL = varPublicas.URL_DESARROLLO + "http://api.androidhive.info/json/";
    private static Retrofit retrofit = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://181.65.211.138:8089/chat/lhc_web/webservice/")
                    //.baseUrl(varPublicas.URL_DESARROLLO +"listarTicket")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
