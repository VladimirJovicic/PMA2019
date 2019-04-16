package com.example.donesiklon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RestorauntMenu extends AppCompatActivity {
    String naslov;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restoraunt_menu);
        LinearLayout menu_item1 = findViewById(R.id.menu_item1);
        LinearLayout content = (LinearLayout) menu_item1.getChildAt(1);
        final TextView textViewNaslov = (TextView) content.getChildAt(0);
        naslov = (String)textViewNaslov.getText();
        menu_item1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               // Intent intent = new Intent(RestorauntMenu.this, RestaurantListFragment.class);
               // RestorauntMenu.this.startActivity(intent);
                message();

                return true;
            }
        });
    }

    public void message() {

        Toast.makeText(this, "Jelo '"+naslov + "' je dodato u korpu" , Toast.LENGTH_LONG).show();
    }
}
