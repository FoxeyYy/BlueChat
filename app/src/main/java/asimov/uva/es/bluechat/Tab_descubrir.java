package asimov.uva.es.bluechat;

/**
 * Created by Guti on 31/10/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Tab_descubrir extends Fragment {

    /**
     * Lista donde añadir tarjetas de personas
     */
    private LinearLayout lista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_descubrir, container, false);

        lista = (LinearLayout) rootView.findViewById(R.id.lista_descubrir);

        //TODO Stub
        for(int i = 0; i < 9; i++)
            inflater.inflate(R.layout.tarjeta_contacto, lista);

        return rootView;
    }


}
