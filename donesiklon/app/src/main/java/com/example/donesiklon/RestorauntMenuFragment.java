
package com.example.donesiklon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RestorauntMenuFragment extends Fragment {

    private String naslov = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restoraunt_menu, container, false);
        final LinearLayout app_layer = (LinearLayout) view.findViewById(R.id.meni_items);

        LinearLayout menu_item1 = view.findViewById(R.id.menu_item2);
        LinearLayout content = (LinearLayout) menu_item1.getChildAt(1);
        final TextView textViewNaslov = (TextView) content.getChildAt(0);
        naslov = (String)textViewNaslov.getText();

        app_layer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                message();
                return true;
            }
        });
        return view;
    }

    private void message() {
        Toast.makeText(this.getActivity(), "Jelo '"+naslov + "' je dodato u korpu" , Toast.LENGTH_LONG).show();
    }
}

