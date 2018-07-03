package chat.atc.tges.tgeschat.network;

import java.util.List;

import chat.atc.tges.tgeschat.model.Message;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    //@POST(“users/new”)
    @POST("listarTicket")
    Call<List<Message>> getInbox();
}
