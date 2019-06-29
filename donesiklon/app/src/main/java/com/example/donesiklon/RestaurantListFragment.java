package com.example.donesiklon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donesiklon.gps.Info;
import com.example.donesiklon.gps.Utils;
import com.example.donesiklon.model.Restaurant;
import com.example.donesiklon.model.VisitHistory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.Date;
import java.util.List;
public class RestaurantListFragment extends Fragment {

    public String naslov = "";
    int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    int height = Resources.getSystem().getDisplayMetrics().heightPixels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaraunt_list, container, false);
        final LinearLayout layout = view.findViewById(R.id.rest_list);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Location usersLocation = getUserLocation();

        db.collection("restoraunts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final Restaurant restaurant = createRestoraunt(document);
                        final Info info = calculateDistance(usersLocation, restaurant);

                        LinearLayout restorauntLayout = createRestorauntLayout(restaurant, info);
                        restorauntLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addToVisitHistory(db,restaurant.getId());
                                Fragment fragment = new RestorauntMenuFragment();
                                Bundle args = new Bundle();
                                args.putString("id", restaurant.getId());
                                fragment.setArguments(args);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                        });

                        restorauntLayout.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                Fragment fragment = new RestaurantReview();
                                Bundle args = new Bundle();
                                args.putString("id", restaurant.getId());
                                fragment.setArguments(args);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                return true;
                            }
                        });
                        layout.addView(restorauntLayout);
                    }
                } else {
                    Log.d("qwe", "Error getting documents: ", task.getException());
                }
            }
        });
        return view;
    }

    private Restaurant createRestoraunt(QueryDocumentSnapshot document) {
        Restaurant retVal = new Restaurant();
        retVal.setId(document.getId());
        retVal.setName(document.getData().get("name").toString());
        retVal.setAddress(document.getData().get("address").toString());
        retVal.setImageUrl(document.getData().get("imageUrl").toString());
        retVal.setDescription(document.getData().get("description").toString());

        Address addressInfo = getLocationFromAddress(retVal.getAddress());
        if(addressInfo!=null) {
            Log.i("addressInfo", addressInfo.toString());
            retVal.setLat(addressInfo.getLatitude());
            retVal.setLon(addressInfo.getLongitude());
        }

        return  retVal;
    }

    public Address getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(getActivity().getApplicationContext());
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

                Log.i("lat", String.valueOf(location.getLatitude()));
                Log.i("lon", String.valueOf(location.getLongitude()));

                p1 = new GeoPoint(
                        (location.getLatitude()),(location.getLongitude()));

                return location;
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    LocationManager locationManager;
    LocationListener locationListener;

    public Location getUserLocation(){

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
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

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.i("users location", String.valueOf(String.valueOf( lastKnownLocation.getLatitude())+','+String.valueOf(lastKnownLocation.getLongitude())));
            return lastKnownLocation;
        } else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return null;
        }
    }

    public static GeoPoint convert(double latitude, double longitude){
        int lat = (int)(latitude * 1E6);
        int lng = (int)(longitude * 1E6);

        return new GeoPoint(lat, lng);
    }

    public Info calculateDistance(Location usersLocation, Restaurant restaurant){
        //double fullDistance = Utils.distance(usersLocation.getLatitude(),restaurant.getLat(),usersLocation.getLongitude(),restaurant.getLon(), 0, 0);

        Info info =  Utils.getRealDistance(
                usersLocation.getLatitude(),usersLocation.getLongitude(),
                restaurant.getLat(),restaurant.getLon());


        return info;
    }

    private LinearLayout createRestorauntLayout(Restaurant restaurant, Info info) {
        LinearLayout restorauntLayout = new LinearLayout(getActivity().getApplicationContext());
        restorauntLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height/5);
        layoutParams.setMargins(5, 10, 10, 30);
        restorauntLayout.setLayoutParams(layoutParams);
        restorauntLayout.setBackgroundDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.border));

        LinearLayout imageHolder = new LinearLayout(getActivity().getApplicationContext());
        imageHolder.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));

        ImageView imageView = new ImageView(getActivity().getApplicationContext());
        TableRow.LayoutParams layoutParamsForImageView = new TableRow.LayoutParams(width/3, height/5 - 20);
        layoutParamsForImageView.setMargins(10, 10, 0, 20);
        imageView.setLayoutParams(layoutParamsForImageView);

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(getActivity().getApplicationContext()).load(restaurant.getImageUrl()).into(imageView);
        imageHolder.addView(imageView);

        ImageView image = new ImageView(getActivity().getApplicationContext());
        Glide.with(getActivity().getApplicationContext()).load(restaurant.getImageUrl()).into(image);

        LinearLayout textViewsHolder = new LinearLayout(getActivity().getApplicationContext());
        LinearLayout.LayoutParams layoutParamsContentHolderLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParamsContentHolderLayout.setMargins(25, 10, 25, 0);
        textViewsHolder.setOrientation(LinearLayout.VERTICAL);
        textViewsHolder.setLayoutParams(layoutParamsContentHolderLayout);

        TextView textName = new TextView(getActivity().getApplicationContext());
        textName.setTextSize(20);
        textName.setTypeface(null, Typeface.BOLD);
        textName.setText(restaurant.getName());

        TextView textAddress = new TextView(getActivity().getApplicationContext());
        textAddress.setTextSize(13);
        textAddress.setText(this.getString(R.string.address) + restaurant.getAddress());

        TextView textDescription = new TextView(getActivity().getApplicationContext());
        textDescription.setTextSize(13);
        textDescription.setText(this.getString(R.string.description) + restaurant.getDescription());

        TextView textDistance = new TextView(getActivity().getApplicationContext());
        textDistance.setTextSize(13);
        textDistance.setText(this.getString(R.string.distance) + info.getDistance());

        TextView textDelivery = new TextView(getActivity().getApplicationContext());
        textDelivery.setTextSize(13);
        textDelivery.setText(this.getString(R.string.deliveryTime) + info.getDuration());

        textViewsHolder.addView(textName);
        textViewsHolder.addView(textAddress);
        textViewsHolder.addView(textDescription);
        textViewsHolder.addView(textDistance);
        textViewsHolder.addView(textDelivery);

        restorauntLayout.addView(imageHolder);
        restorauntLayout.addView(textViewsHolder);

        return restorauntLayout;
    }

    public void addToVisitHistory(FirebaseFirestore db, String restarauntId) {
        VisitHistory visitHistory = new VisitHistory();
        visitHistory.setDate(new Date());
        visitHistory.setRestorauntId(restarauntId);
        visitHistory.setUserId(SaveSharedPreference.getUserName(getActivity().getApplicationContext()));
        db.collection("visit_history").add(visitHistory);
    }


}
