package com.example.donesiklon;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class RestaurantListFragment extends Fragment {

    public String naslov = "";
    int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    int height = Resources.getSystem().getDisplayMetrics().heightPixels;

    public static List<Restaurant> restaurants = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        restaurants = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_restaraunt_list, container, false);
        final LinearLayout layout = view.findViewById(R.id.restoraunt_list_layout);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Location usersLocation =  getUserLocation();
        db.collection("restoraunts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final Restaurant restaurant = createRestoraunt(document);
                        restaurants.add(restaurant);
                        final Info info = new Info();// = calculateDistance(usersLocation, restaurant);

                        LinearLayout restorauntLayout = createRestorauntLayout(restaurant, info);
                        restorauntLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addToVisitHistory(db,restaurant.getId());
                                Fragment fragment = new RestorauntMenuFragment();
                                Bundle args = new Bundle();
                                args.putString("id", restaurant.getId());
                                args.putString("restAddress", restaurant.getAddress());
                                fragment.setArguments(args);
                                FragmentManager fragmentManager = ((MainActivity)mActivity).getSupportFragmentManager();
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
                                args.putString("restName", restaurant.getName());
                                args.putString("restAddress", restaurant.getAddress());
                                fragment.setArguments(args);
                                FragmentManager fragmentManager = ((MainActivity)mActivity).getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, fragment, "REVIEW_FRAG");
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

        final SearchView searchView = view.findViewById(R.id.search_text);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("prvi",s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("drugi",s);
                final String query = s;
                db.collection("restoraunts")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            layout.removeAllViews();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("name").toString().trim().toLowerCase().contains(query.trim().toLowerCase())) {
                                    final Restaurant restaurant = createRestoraunt(document);
                                    final Info info = new Info(); //calculateDistance(usersLocation, restaurant);
                                    LinearLayout restorauntLayout = createRestorauntLayout(restaurant, info);
                                    restorauntLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            addToVisitHistory(db, restaurant.getId());
                                            Fragment fragment = new RestorauntMenuFragment();
                                            Bundle args = new Bundle();
                                            args.putString("id", restaurant.getId());
                                            args.putString("restAddress", restaurant.getAddress());
                                            fragment.setArguments(args);
                                            FragmentManager fragmentManager = ((MainActivity)mActivity).getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.fragment_container, fragment);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                        }
                                    });
                                    layout.addView(restorauntLayout);
                                }
                            }
                        }
                    }
                });
                return false;
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

        retVal.setLat(0);
        retVal.setLon(0);

        return  retVal;
    }

    public Address getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(((MainActivity)mActivity).getApplicationContext());
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

    private Activity mActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity = (Activity) context;
        }
    }

    LocationManager locationManager;
    LocationListener locationListener;

    private void showLocationDisabledInfo() {
        final Context c = (MainActivity)mActivity;
        final Builder builder = new AlertDialog.Builder(c);
        final AlertDialog alert = builder.create();
        builder.setMessage("GPS is disabled - Open Settings?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int id) {
                dialog.cancel();
                dialog.dismiss();
                alert.cancel();
                alert.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                c.startActivity(intent);
            }
        });
        builder.setNeutralButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                mActivity.finish();
                dialog.cancel();
                dialog.dismiss();
                alert.cancel();
                alert.dismiss();
            }
        });
        alert.show();
    }


    public Location getUserLocation(){

        locationManager = (LocationManager)((MainActivity)mActivity).getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
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

        if (ContextCompat.checkSelfPermission(((MainActivity)mActivity).getApplicationContext(),
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

               // showLocationDisabledInfo();
//                Intent newIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                ((MainActivity)mActivity).startActivity(newIntent); //ako nema providera otvori mu
//                Log.i("entered","da");
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
            ActivityCompat.requestPermissions(((MainActivity)mActivity),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return null;
        }
    }

    public Location myLocation;
    public void getAddress(Location location){
         this.myLocation = location;
    }

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

    private LinearLayout createRestorauntLayout(final Restaurant restaurant, Info info) {
        LinearLayout restorauntLayout = new LinearLayout(((MainActivity)mActivity).getApplicationContext());
        restorauntLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height/5);
        layoutParams.setMargins(5, 10, 10, 30);
        restorauntLayout.setLayoutParams(layoutParams);
        restorauntLayout.setBackgroundDrawable(ContextCompat.getDrawable(((MainActivity)mActivity).getApplicationContext(), R.drawable.border));

        LinearLayout imageHolder = new LinearLayout(((MainActivity)mActivity).getApplicationContext());
        imageHolder.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));

        ImageView imageView = new ImageView(((MainActivity)mActivity).getApplicationContext());
        TableRow.LayoutParams layoutParamsForImageView = new TableRow.LayoutParams(width/3, height/5 - 20);
        layoutParamsForImageView.setMargins(10, 10, 0, 20);
        imageView.setLayoutParams(layoutParamsForImageView);

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(((MainActivity)mActivity).getApplicationContext()).load(restaurant.getImageUrl()).into(imageView);
        imageHolder.addView(imageView);

        ImageView image = new ImageView(((MainActivity)mActivity).getApplicationContext());
        Glide.with(((MainActivity)mActivity).getApplicationContext()).load(restaurant.getImageUrl()).into(image);

        LinearLayout textViewsHolder = new LinearLayout(((MainActivity)mActivity).getApplicationContext());
        LinearLayout.LayoutParams layoutParamsContentHolderLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParamsContentHolderLayout.setMargins(25, 10, 25, 0);
        textViewsHolder.setOrientation(LinearLayout.VERTICAL);
        textViewsHolder.setLayoutParams(layoutParamsContentHolderLayout);

        TextView textName = new TextView(((MainActivity)mActivity).getApplicationContext());
        textName.setTextSize(20);
        textName.setTypeface(null, Typeface.BOLD);
        textName.setText(restaurant.getName());

        TextView textAddress = new TextView(((MainActivity)mActivity).getApplicationContext());
        textAddress.setTextSize(13);
        textAddress.setText(restaurant.getAddress());

        TextView textDescription = new TextView(((MainActivity)mActivity).getApplicationContext());
        textDescription.setTextSize(13);
        textDescription.setText(((MainActivity)mActivity).getString(R.string.description) +" "+ restaurant.getDescription());

        TextView textDistance = new TextView(((MainActivity)mActivity).getApplicationContext());
        textDistance.setTextSize(13);
        textDistance.setText(((MainActivity)mActivity).getString(R.string.distance) +" "+ "Long press for details");

        TextView textDelivery = new TextView(((MainActivity)mActivity).getApplicationContext());
        textDelivery.setTextSize(13);
        textDelivery.setText(((MainActivity)mActivity).getString(R.string.deliveryTime) + " "+ "...");

        LinearLayout ceo = new LinearLayout(((MainActivity)mActivity).getApplicationContext());

        LinearLayout.LayoutParams zaCeo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
        ceo.setLayoutParams(zaCeo);
        ceo.addView(textName);

        textViewsHolder.addView(ceo);
        textViewsHolder.addView(textAddress);
        textViewsHolder.addView(textDescription);
        textViewsHolder.addView(textDistance);
        textViewsHolder.addView(textDelivery);

        restorauntLayout.addView(imageHolder);
        restorauntLayout.addView(textViewsHolder);

        return restorauntLayout;
    }

    public void addToVisitHistory(final FirebaseFirestore db, String restarauntId) {
        final VisitHistory visitHistory = new VisitHistory();
        visitHistory.setDate(new Date());
        visitHistory.setRestorauntId(restarauntId);
        visitHistory.setUserId(SaveSharedPreference.getUserName(((MainActivity)mActivity).getApplicationContext()));
        db.collection("visit_history").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean toAdd = true;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(document.get("userId").equals(visitHistory.getUserId()) && document.get("restorauntId").equals(visitHistory.getRestorauntId())) {
                        toAdd = false;
                        break;
                    }
                }
                if(toAdd) {
                    db.collection("visit_history").add(visitHistory);
                }
            }
        });

    }


}