package com.example.donesiklon;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donesiklon.model.Restaurant;
import com.example.donesiklon.model.VisitHistory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class RestaurantListFragment extends Fragment {

    public String naslov = "";
    int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    int height = Resources.getSystem().getDisplayMetrics().heightPixels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaraunt_list, container, false);
        final LinearLayout layout = view.findViewById(R.id.restoraunt_list_layout);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("restoraunts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final Restaurant restaurant = createRestoraunt(document);
                        LinearLayout restorauntLayout = createRestorauntLayout(restaurant);
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
                                    LinearLayout restorauntLayout = createRestorauntLayout(restaurant);
                                    restorauntLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            addToVisitHistory(db, restaurant.getId());
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
        return  retVal;
    }

    private LinearLayout createRestorauntLayout(Restaurant restaurant) {
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
        textAddress.setText(R.string.address + restaurant.getAddress());

        TextView textDescription = new TextView(getActivity().getApplicationContext());
        textDescription.setTextSize(13);
        textDescription.setText(R.string.description + restaurant.getDescription());

        textViewsHolder.addView(textName);
        textViewsHolder.addView(textAddress);
        textViewsHolder.addView(textDescription);

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
