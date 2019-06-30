package com.example.donesiklon.gps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.example.donesiklon.MainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RestaurantDirections {
    public static Direction direction;

     public static void getDirections(double startLat, double startLon, double endLat, double endLon) {
        GoogleDirection.withServerKey("AIzaSyAU8awJCP75PAy9AcocqyJXNtinbEn5CnE")
                .from(new LatLng(startLat, startLon))
                .to(new LatLng(endLat, endLon))
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            // Do something
                            Log.i("onDirectionSucces",direction.getRouteList().toString());
                            for(Route o : direction.getRouteList()) {
                                Log.i("->" ,o.getLegList().get(0).getDirectionPoint().toString());
                            }

                            RestaurantDirections.direction=direction;
                        } else {
                            // Do something
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                    }
                });
    }

    public void getMultiple(){
        List<LatLng> waypoints = Arrays.asList(
                new LatLng(41.8766061, -87.6556908),
                new LatLng(41.8909056, -87.6467561)
        );
        GoogleDirection.withServerKey("AIzaSyAU8awJCP75PAy9AcocqyJXNtinbEn5CnE")
                .from(new LatLng(41.8838111, -87.6657851))
                .and(waypoints)
                .to(new LatLng(41.9007082, -87.6488802))
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if(direction.isOK()) {
                            // Do something
                        } else {
                            // Do something
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                    }
                });
    }

    public static Address getLocationFromAddress(String strAddress, Activity activity){

        Geocoder coder = new Geocoder(activity.getApplicationContext());
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

    static LocationManager locationManager;
    static  LocationListener locationListener;

    public static Location getUserLocation(Activity activity){

        locationManager = (LocationManager)((MainActivity)activity).getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //centreMapOnLocation(location,"Your Location");
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

        if (ContextCompat.checkSelfPermission(((MainActivity)activity).getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){



            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            List<String> lProviders = locationManager.getProviders(false);
            for(int i=0; i<lProviders.size(); i++){
                Log.d("LocationActivity", lProviders.get(i));
            }
            String provider = locationManager.getBestProvider(criteria, true); // true->enabled providers only

            locationManager.requestLocationUpdates(provider,0,0,locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(provider);

            if(lastKnownLocation!=null) {
                Log.i("users location", String.valueOf(String.valueOf(lastKnownLocation.getLatitude()) + ',' + String.valueOf(lastKnownLocation.getLongitude())));
                return lastKnownLocation;

            }

            if(lastKnownLocation==null){
                Intent newIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ((MainActivity)activity).startActivity(newIntent); //ako nema providera otvori mu
                Log.i("entered","da");
            }

            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            lProviders = locationManager.getProviders(false);
            for(int i=0; i<lProviders.size(); i++){
                Log.d("LocationActivity", lProviders.get(i));
            }
            provider = locationManager.getBestProvider(criteria, true); // true->enabled providers only

            locationManager.requestLocationUpdates(provider,0,0,locationListener);
            lastKnownLocation = locationManager.getLastKnownLocation(provider);

            if(lastKnownLocation!=null) {
                Log.i("users location", String.valueOf(String.valueOf(lastKnownLocation.getLatitude()) + ',' + String.valueOf(lastKnownLocation.getLongitude())));
                return lastKnownLocation;

            }

            return null;
        } else {
            ActivityCompat.requestPermissions(((MainActivity)activity),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return null;
        }
    }


}
