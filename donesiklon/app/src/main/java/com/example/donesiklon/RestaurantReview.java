package com.example.donesiklon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;


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
        return view;

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

}
