package chat.atc.tges.tgeschat.Services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import chat.atc.tges.tgeschat.varPublicas.varPublicas;

public class FireBaseId extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        varPublicas.tokenMovil = FirebaseInstanceId.getInstance().getToken();
    }
}
