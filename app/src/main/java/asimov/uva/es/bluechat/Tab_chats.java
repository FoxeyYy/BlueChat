package asimov.uva.es.bluechat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Guti on 31/10/2016.
 */

public class Tab_chats extends Fragment {

    /**
     * Lista donde añadir tarjetas de personas
     */
    private LinearLayout lista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);

        lista = (LinearLayout) rootView.findViewById(R.id.lista_chats);

        //TODO Stub
        for(int i = 0; i < 4; i++)
            inflater.inflate(R.layout.tarjeta_contacto, lista);

        return rootView;
    }
}
