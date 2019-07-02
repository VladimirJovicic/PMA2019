package com.example.donesiklon;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.donesiklon.gps.Info;
import com.example.donesiklon.gps.Utils;
import com.example.donesiklon.model.Restaurant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;
    LocationManager locationManager;
    LocationListener locationListener;
    private MarkerOptions options = new MarkerOptions();
    public List<Restaurant> restsWithLoc = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        restsWithLoc = new ArrayList<>();




        Address addressInfo;

        Log.i("size: ", String.valueOf(RestaurantListFragment.restaurants.size()));

        for(Restaurant r : RestaurantListFragment.restaurants){
            Log.i("restIs:",r.getAddress());
            addressInfo = getLocationFromAddress(r.getAddress());
            if(addressInfo!=null) {
                Log.i("addressInfo", addressInfo.toString());
                r.setLat(addressInfo.getLatitude());
                r.setLon(addressInfo.getLongitude());
                restsWithLoc.add(r);
            }
           // info = calculateDistance(usersLocation, r);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_act);
        mapFragment.getMapAsync(this);
    }
    private Activity mActivity;

    public Info calculateDistance(Location usersLocation, Restaurant restaurant){

        Info info = new Info();


        if(usersLocation !=null) {
            info = Utils.getRealDistance(
                    usersLocation.getLatitude(), usersLocation.getLongitude(),
                    restaurant.getLat(), restaurant.getLon());
            return info;
        }

        info.setDistance("Not Determined");
        info.setDuration("Not Known");

        return info;
    }

    public Address getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this.getApplicationContext());
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            if(address.size()>0) {
                Address location = address.get(0);
                Log.i("address",location.toString());
                location.getLatitude();
                location.getLongitude();

                return location;
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        if (intent.getIntExtra("Place Number",0) == 0 ){

            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                   // centreMapOnLocation(location,getResources().getString(R.string.yourLocation));
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMapOnLocation(lastKnownLocation,getResources().getString(R.string.yourLocation));
            } else {

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }

    }
    Info info = null;
    public void centreMapOnLocation(Location location, String title){
        if(location!=null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();

              LatLng latLng;
            for (Restaurant r : restsWithLoc) {
                latLng = new LatLng(r.getLat(),r.getLon());
                 info =calculateDistance(location, r);
                options.position(latLng);
                options.title(r.getName() );
                 options.snippet(r.getAddress()+"\n"+this.getString(R.string.distance)+" "+info.getDistance()
                         +"\n"+this.getString(R.string.deliveryTime)+" "+info.getDuration()+"\n"+r.getDescription());
                mMap.addMarker(options);






            }

            mMap.addMarker(new MarkerOptions().position(userLocation).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(getApplicationContext());
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(getApplicationContext());
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(getApplicationContext());
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });


            mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    LatLng latLon = marker.getPosition();


                    for(Restaurant r : RestaurantListFragment.restaurants){
                        if (latLon.latitude == r.getLat() && latLon.longitude == r.getLon()){


                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.putExtra("frgToLoad", 1);
                            i.putExtra("id", r.getId());
                            i.putExtra("name", r.getName());
                            i.putExtra("address", r.getAddress());
                            // Now start your activity
                            startActivity(i);
                            finish();







                        }

                    }
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastKnownLocation!=null)
                    centreMapOnLocation(lastKnownLocation,getResources().getString(R.string.yourLocation));
            }
        }
    }

}
