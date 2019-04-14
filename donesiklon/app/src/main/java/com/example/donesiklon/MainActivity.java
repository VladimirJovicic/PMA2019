package com.example.donesiklon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private MapsActivity mapsActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.restoraunt);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //implementiran interfejs
        navigationView.setNavigationItemSelectedListener(this);

        //za ikonicu navigation drawera
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //da se ne bi prikazala prazna aktivnost na pocetku
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RestaurantListFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_restaurant_list);


    }

    //kad se klikne back dok je u navigation draweru treba da zatvori navigation drawer a ne da ode back
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_restaurant_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RestaurantListFragment()).addToBackStack(null).commit();
                break;
            case R.id.shopping_cary:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShoppingCart()).addToBackStack(null).commit();
                break;
            case R.id.visit_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VisitHistory()).addToBackStack(null).commit();
                break;
            case R.id.map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Map()).addToBackStack(null).commitAllowingStateLoss();
                //with activity:
                //Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                //MainActivity.this.startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}