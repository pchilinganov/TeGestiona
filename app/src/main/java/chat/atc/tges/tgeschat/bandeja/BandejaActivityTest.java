package chat.atc.tges.tgeschat.bandeja;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.R;
import android.view.View.OnClickListener;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;


public class BandejaActivityTest extends BaseVolleyActivity implements OnClickListener{

    Button btnHistorial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.layout_bandeja);

        btnHistorial = (Button) findViewById(R.id.bTHistorial);
        btnHistorial.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
            if (v==btnHistorial){
                varPublicas.estadoHistorialTicket=1;
                Intent intent = new Intent(BandejaActivityTest.this, Mensajeria.class);
                startActivity(intent);
            }
    }
}
