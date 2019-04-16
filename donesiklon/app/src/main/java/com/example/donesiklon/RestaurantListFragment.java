package com.example.donesiklon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/* DisplayMetrics displayMetrics = new DisplayMetrics();
 ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
 int height = displayMetrics.heightPixels;
 int width = displayMetrics.widthPixels;


         ScrollView scrollView = new ScrollView(container.getContext());

         scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

         LinearLayout parent = new LinearLayout(container.getContext());
 for(int i = 0; i < 10; i++) {
         parent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
         parent.setOrientation(LinearLayout.VERTICAL);

         LinearLayout card = new LinearLayout(parent.getContext());
         card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height / 6));
         card.setOrientation(LinearLayout.HORIZONTAL);

         LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();
         params.setMargins(5, 10, 10, 35);
         card.setLayoutParams(params);

         ImageView imageView = new ImageView(card.getContext());
         switch (i) {
             case 0:imageView.setImageResource(R.drawable.food1);break;
             case 1:imageView.setImageResource(R.drawable.food2);break;
             case 2:imageView.setImageResource(R.drawable.food4);break;
             case 3:imageView.setImageResource(R.drawable.food3);break;
             case 4:imageView.setImageResource(R.drawable.food5);break;
             case 5:imageView.setImageResource(R.drawable.chinese1);break;
             case 6:imageView.setImageResource(R.drawable.chinese2);break;
             case 7:imageView.setImageResource(R.drawable.chinese3);break;
             case 8:imageView.setImageResource(R.drawable.chinese4);break;
             case 9:imageView.setImageResource(R.drawable.chinese5);break;
         }

         imageView.setScaleType(ImageView.ScaleType.FIT_XY);

         LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width / 3, ViewGroup.LayoutParams.MATCH_PARENT);
         lp.setMargins(10, 10, 10, 10);
         imageView.setLayoutParams(lp);

         TextView textView = new TextView(card.getContext());
         LinearLayout.LayoutParams lpForTextView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
         lpForTextView.setMargins(10, 0, 0, 0);
         textView.setText("Nase najbolje ponude uz najbolje ukuse sa najboljom hranom i nemam vise ideje sta da pisem vise ovde jer sam ostao bez inspiracija ali bitno je smao da napunim sa tekstom da vidim kako radi wrap i kada ima puno teksta. Samo provera da vidim kako izgleda kako izgleda kada ima vise teksta kada se neko malo proserava za opis kao ja upravo");
         ScrollView scrollViewForText = new ScrollView(card.getContext());
         scrollViewForText.addView(textView);


         LinearLayout naslovLayout = new LinearLayout(parent.getContext());
         final TextView naslov = new TextView(naslovLayout.getContext());
         naslov.setText("Naslov_" + i);
         naslov.setTextSize(18);
         naslov.setTypeface(null, Typeface.BOLD);
         naslov.setTextColor(Color.rgb(0,0,0));
         naslovLayout.addView(naslov);

         card.addView(imageView);
         card.addView(scrollViewForText);
         card.setBackgroundResource(R.drawable.border);



         parent.addView(naslovLayout);
         parent.addView(card);
 }
         scrollView.addView(parent);

     container.addView(scrollView);*/
    public class RestaurantListFragment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_restaraunt_list);
        LinearLayout restoraun1 = findViewById(R.id.restoraunt1);

        restoraun1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantListFragment.this, RestorauntMenu.class);
                RestaurantListFragment.this.startActivity(intent);
            }
        });

    }

}
