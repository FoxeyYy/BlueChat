package asimov.uva.es.bluechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import asimov.uva.es.bluechat.Dominio.Contacto;

public class ActivityCrearGrupo extends AppCompatActivity implements View.OnClickListener {

    /**
     * Lista de contactos
     */
    private List<Contacto> contactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogo_crear_grupo);

        findViewById(R.id.boton_crear).setOnClickListener(this);
        findViewById(R.id.boton_cancelar).setOnClickListener(this);

        contactos = Contacto.getContactos(this);
        mostrarContactos();

    }

    private void mostrarContactos () {
        ListView itemsList = (ListView) findViewById(R.id.lista_contactos);
        itemsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        Contacto[] fuente = new Contacto[contactos.size()];
        fuente = contactos.toArray(fuente);
        final int[] destino = {android.R.id.text1};

        ArrayAdapter<Contacto> adaptador = new ArrayAdapter(this,
                android.R.layout.simple_list_item_multiple_choice,
                fuente);

        itemsList.setAdapter(adaptador);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boton_crear:
                crearGrupo();
                finish();
                break;
            case R.id.boton_cancelar:
                finish();
                break;
            default:
                break;
        }
    }

    private void crearGrupo() {
        List<Contacto> seleccionados = getSeleccionados();
        String nombreGrupo = getNombreGrupo();
        if (0 == seleccionados.size()) {
            Toast toast = Toast.makeText(this, getString(R.string.error_contactos_vacio), Toast.LENGTH_SHORT);
            toast.show();
        } else if (nombreGrupo.isEmpty()) {
            Toast toast = Toast.makeText(this, getString(R.string.error_nombre_grupo_vacio), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Intent chat = new Intent(this, ActivityChatGrupal.class);
            chat.putParcelableArrayListExtra(ActivityChatGrupal.CONTACTOS, (ArrayList) seleccionados);
            chat.putExtra(ActivityChatGrupal.NOMBRE_GRUPO, nombreGrupo);
            startActivity(chat);
        }
    }

    private List<Contacto> getSeleccionados () {
        List<Contacto> seleccionados = new ArrayList<>();
        final ListView itemsList = (ListView) findViewById(R.id.lista_contactos);

        final SparseBooleanArray posicionesSeleccionados = itemsList.getCheckedItemPositions();

        for (int i = 0; i < itemsList.getAdapter().getCount(); i++) {
            if (posicionesSeleccionados.get(i)){
                seleccionados.add(contactos.get(i));
            }
        }

        return seleccionados;
    }

    private String getNombreGrupo () {
        return String.valueOf(((TextView) findViewById(R.id.nombre_grupo)).getText());
    }
}
