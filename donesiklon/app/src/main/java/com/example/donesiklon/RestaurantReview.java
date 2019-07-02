package com.example.donesiklon;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.donesiklon.gps.Info;
import com.example.donesiklon.gps.RestaurantDirections;
import com.example.donesiklon.gps.Utils;
import com.example.donesiklon.model.Restaurant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.Date;
import java.util.List;


public class RestaurantReview extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String id;

    public RestaurantReview() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantReview.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantReview newInstance(String param1, String param2) {
        RestaurantReview fragment = new RestaurantReview();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    Info info;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Bundle args = getArguments();
        id = args.getString("id");
        View view =  inflater.inflate(R.layout.fragment_restaurant_review, container, false);
        Button seeReviews = view.findViewById(R.id.showReviews);
        seeReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ReviewsList();
                Bundle args = new Bundle();
                args.putString("id", id);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        final EditText comment = view.findViewById(R.id.editText);
        final RatingBar rating = view.findViewById(R.id.ratingBar);
        Button submitReview = view.findViewById(R.id.submitReview);
        submitReview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(comment.getText().toString().trim().equals("") || rating.getRating() == 0.0){
                    Toast.makeText(getActivity().getApplicationContext(), R.string.checkFields, Toast.LENGTH_LONG).show();
                }else {
                    com.example.donesiklon.model.RestaurantReview restaurantReview = new com.example.donesiklon.model.RestaurantReview();
                    restaurantReview.setComment(comment.getText().toString().trim());
                    restaurantReview.setDate(new Date().toString());
                    restaurantReview.setRestaurantId(id);
                    restaurantReview.setUserId(SaveSharedPreference.getUserName(getActivity().getApplicationContext()));
                    restaurantReview.setRating(rating.getRating());
                    saveReviewToDatabase(db, restaurantReview);
                }
            }
        });


        id = args.getString("id");
        final String restName = args.getString("restName");
        final String restaurantAddress = args.getString("restAddress");

        info = new Info();
        Address addressInfo = getLocationFromAddress(restaurantAddress);
        Restaurant rest = new Restaurant();
        if(addressInfo!=null) {
            Log.i("addressInfo", addressInfo.toString());
            rest.setLat(addressInfo.getLatitude());
            rest.setLon(addressInfo.getLongitude());
        }

        final Location usersLocation = RestaurantDirections.getUserLocation((MainActivity)mActivity);

        info = calculateDistance(usersLocation, rest);

        Button showDirections = view.findViewById(R.id.showDirections);
        showDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



        String startLat = Double.valueOf(usersLocation.getLatitude()).toString();
        String startLon = Double.valueOf(usersLocation.getLongitude()).toString();



        Address restAddress = RestaurantDirections.getLocationFromAddress(restaurantAddress, (MainActivity)mActivity);


        String endLat = Double.valueOf(restAddress.getLatitude()).toString();
        String endLon = Double.valueOf(restAddress.getLongitude()).toString();


        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("restName",restName);
        bundle.putString("startLat",startLat);
        bundle.putString("startLon",startLon);
        bundle.putString("endLat",endLat);
        bundle.putString("endLon",endLon);
        bundle.putString("distance",info.getDistance());
        bundle.putString("time",info.getDuration());

        Fragment fragment = new Map();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

            }
        });


       // Intent intent2 = new Intent(getActivity().getApplicationContext(), MapsActivity.class);
       // getActivity().getApplicationContext().startActivity(intent2);

//        TextView tv1 = view.findViewById(R.id.distanceTextView);
//
//        tv1.setText(((MainActivity)mActivity).getString(R.string.distance)+" "+info.getDistance()+"\n"+
//                ((MainActivity)mActivity).getString(R.string.deliveryTime)+" "+info.getDuration() );
//
//        TextView tv2 = view.findViewById(R.id.durationTextView);
//        tv2.setText(((MainActivity)mActivity).getString(R.string.deliveryTime)+" "+info.getDuration() );

        return view;

    }

    public Info calculateDistance(Location usersLocation, Restaurant restaurant){
        //double fullDistance = Utils.distance(usersLocation.getLatitude(),restaurant.getLat(),usersLocation.getLongitude(),restaurant.getLon(), 0, 0);

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

    public void saveReviewToDatabase(FirebaseFirestore db,  com.example.donesiklon.model.RestaurantReview review) {

        db.collection("restaraunt_reviews").add(review)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.thankForReview, Toast.LENGTH_LONG).show();
                    }
                });
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


}
