package com.example.donesiklon;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.donesiklon.model.Product;
import com.example.donesiklon.model.RestaurantReview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class ReviewsList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String id;


    public ReviewsList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewsList.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewsList newInstance(String param1, String param2) {
        ReviewsList fragment = new ReviewsList();
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
        Bundle args = getArguments();
        id = args.getString("id");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_reviews_list, container, false);
        final TableLayout tableLayout = view.findViewById(R.id.list_of_reviews);
        db.collection("restaraunt_reviews").
                whereEqualTo("restaurantId", id).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size() == 0) {
                        tableLayout.addView(createEmptyMenuLayout());
                    }else {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RestaurantReview restaurantReview;
                            restaurantReview = createReview(document);
                            tableLayout.addView(createTableRow(restaurantReview));
                        }
                    }
                }else {
                    Log.d("qwe", "Error getting documents: ", task.getException());
                }
            }
        });
        return view;
    }

    private RestaurantReview createReview(QueryDocumentSnapshot document){
        RestaurantReview restaurantReview = new RestaurantReview();
        restaurantReview.setRating(Float.parseFloat(document.get("rating").toString()));
        restaurantReview.setUserId(document.get("userId").toString());
        restaurantReview.setRestaurantId(document.get("restaurantId").toString());
        SimpleDateFormat format = new SimpleDateFormat();
        restaurantReview.setDate(document.get("date").toString());
        restaurantReview.setUserId(SaveSharedPreference.getUserName(getActivity().getApplicationContext()));
        restaurantReview.setComment(document.get("comment").toString());
        return restaurantReview;
    }

    private TableRow createTableRow(RestaurantReview restaurantReview) {
        TableRow row = new TableRow(getActivity().getApplicationContext());

        TextView user = new TextView(getActivity().getApplicationContext());
        user.setText(restaurantReview.getUserId());
        user.setTextSize(18);

        TextView comment = new TextView(getActivity().getApplicationContext());
        comment.setText(restaurantReview.getComment());
        comment.setTextSize(18);
        RatingBar ratingBar = new RatingBar(getActivity().getApplicationContext(), null, android.R.attr.ratingBarStyleSmall);
        ratingBar.setClickable(false);
        ratingBar.setIsIndicator(true);
        ratingBar.setNumStars(5);
        ratingBar.setRating(restaurantReview.getRating());

        row.addView(comment);
        row.addView(ratingBar);
        return row;
    }

    private LinearLayout createEmptyMenuLayout() {
        LinearLayout retValLayout = new LinearLayout(getActivity().getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.MATCH_PARENT);
        retValLayout.setLayoutParams(layoutParams);
        TextView message = new TextView(getActivity().getApplicationContext());
        message.setTextSize(20);
        message.setTypeface(null, Typeface.BOLD);
        message.setText("Trenutna lista recenzija je prazna");
        message.setGravity(Gravity.CENTER);
        retValLayout.addView(message);
        return retValLayout;
    }

}
