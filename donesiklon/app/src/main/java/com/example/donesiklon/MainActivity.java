package com.example.donesiklon;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.donesiklon.gps.Info;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private MapsActivity mapsActivity;
    LocationManager locationManager;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.restoraunt);
        setContentView(R.layout.activity_main);



        // Provera da li postoji sacuvan token - username na onovu koga cemo filtrirati view History, Order...
        String user="";
        if(SaveSharedPreference.getUserName(MainActivity.this).length() == 0)
        {
            Log.i("DA_LI_POSTOJI_USER", "ne");
            // Ako ne postoji VRATI GA NA FORMU ZA LOGOVANJE
        }
        else
        {
            Log.i("DA_LI_POSTOJI_USER", "da");
            user = SaveSharedPreference.getUserName(MainActivity.this);
            Log.i("ZAKACEN_USER", user);
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Username korisnika prikazan na nav
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.usernameNav);
        navUsername.setText(user);

        //implementiran interfejs
        navigationView.setNavigationItemSelectedListener(this);

        //za ikonicu navigation drawera
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //da se ne bi prikazala prazna aktivnost

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.i("Allowed","No");
            return;
        } else {
            Log.i("Allowed","Already Yes");

            RestaurantListFragment list = new RestaurantListFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("fetched", fetched);
            list.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, list).commit();
            navigationView.setCheckedItem(R.id.nav_restaurant_list);
        }

    }

    ArrayList<Parcelable> fetched = new ArrayList<>();
    static ArrayList<Info> alreadyFetched = new ArrayList<>();


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted,
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates("gps", 400, 1, myLocationListener);
                    }

                } else {

                    // permission denied

                }
                return;
            }

        }
    }

    LocationListener myLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("dj","on location changed: "+location.getLatitude()+" & "+location.getLongitude());
            //toastLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }


    };

    @Override
    protected void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates("gps", 400, 1, myLocationListener);

            Log.i("Allowed","Now_yes");
            RestaurantListFragment list = new RestaurantListFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("fetched", fetched);
            list.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, list).commit();
            navigationView.setCheckedItem(R.id.nav_restaurant_list);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(myLocationListener);
        }
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
                RestaurantListFragment list = new RestaurantListFragment();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("fetched", fetched);
                list.setArguments(bundle);



                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, list).addToBackStack(null).commit();
                break;
            case R.id.shopping_cary:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShoppingCart()).addToBackStack(null).commit();
                break;
            case R.id.visit_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VisitHistory()).addToBackStack(null).commit();
                break;
            case R.id.map:
                //with fragment:
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Map()).addToBackStack(null).commitAllowingStateLoss();
                //with activity:
                Intent intent2 = new Intent(MainActivity.this, MapsActivity.class);
                MainActivity.this.startActivity(intent2);
                break;
            case R.id.orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Orders()).addToBackStack(null).commit();
                break;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Settings()).addToBackStack(null).commit();
                break;
            case R.id.nav_logout:
                SaveSharedPreference.clearUserName(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(intent);
                //da back posle login-a ne vrati na main activity
                finish();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }









}
