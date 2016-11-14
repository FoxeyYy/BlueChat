package asimov.uva.es.bluechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Actividad para los chats interactivos
 * @author hector
 */
public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);
        Bundle params = getIntent().getExtras();
        ((TextView) findViewById(R.id.nombre_contacto)).setText(params.getString("nombre_contacto"));
    }
}
