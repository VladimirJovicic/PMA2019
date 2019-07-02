package com.example.donesiklon;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donesiklon.gps.Info;
import com.example.donesiklon.model.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class VisitHistory extends Fragment {

    private int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int height = Resources.getSystem().getDisplayMetrics().heightPixels;
    private Button clearHistoryButton;
    private List<String> restIds = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.visit, container, false);
        final LinearLayout layout = view.findViewById(R.id.visit_history_layout_list);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        clearHistoryButton = view.findViewById(R.id.clear_history_button);

        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("visit_history")
                        .whereEqualTo("userId", SaveSharedPreference.getUserName(getActivity().getApplicationContext())).
                        get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        layout.removeAllViews();
                    }
                });
            }
        });


        db.collection("visit_history").whereEqualTo("userId", SaveSharedPreference.getUserName(getActivity().getApplicationContext()))
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("asd", "Adding " + document.get("restorauntId").toString());
                    restIds.add(document.get("restorauntId").toString());
                    Log.d("list", String.valueOf(restIds.size()));

                }
                for(final String s : restIds) {
                    Log.d("id", s);
                    db.collection("restoraunts")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().size() == 0) {
                                    layout.addView(createEmptyMenuLayout());
                                }else {
                                    LinearLayout restorauntLayout = new LinearLayout(getActivity().getApplicationContext());
                                    for (QueryDocumentSnapshot documentRestoraunt : task.getResult()) {
                                        if (documentRestoraunt.getId().equals(s)) {
                                            Restaurant restaurant = createRestoraunt(documentRestoraunt);
                                            restorauntLayout = createRestorauntLayout(restaurant);
                                            layout.addView(restorauntLayout);
                                        }
                                    }
                                }
                        }
                        }
                    });
                }
            }
        });

        return view;
    }

    private LinearLayout createEmptyMenuLayout() {
        LinearLayout retValLayout = new LinearLayout(getActivity().getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.MATCH_PARENT);
        retValLayout.setLayoutParams(layoutParams);
        TextView message = new TextView(getActivity().getApplicationContext());
        message.setTextSize(20);
        message.setTypeface(null, Typeface.BOLD);
        message.setText(R.string.emptyVisitsHistory);
        message.setGravity(Gravity.CENTER);
        retValLayout.addView(message);
        return retValLayout;
    }

    private Activity mActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity = (Activity) context;
        }
    }

    private LinearLayout createRestorauntLayout(final Restaurant restaurant) {
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
        LinearLayout levi = new LinearLayout(((MainActivity)mActivity).getApplicationContext());
        LinearLayout desni = new LinearLayout(((MainActivity)mActivity).getApplicationContext());

        LinearLayout.LayoutParams zaCeo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
        ceo.setLayoutParams(zaCeo);

        LinearLayout.LayoutParams leviparams = new LinearLayout.LayoutParams(width/4,  LinearLayout.LayoutParams.WRAP_CONTENT);
        levi.setLayoutParams(leviparams);
        levi.setGravity(Gravity.LEFT);
        LinearLayout.LayoutParams desniParams = new LinearLayout.LayoutParams(width/5,  LinearLayout.LayoutParams.WRAP_CONTENT);
        desniParams.setMargins(60,10,0,0);
        desni.setLayoutParams(desniParams);
        desni.setGravity(Gravity.RIGHT);
        levi.addView(textName);

        ImageView ikonica = new ImageView(((MainActivity)mActivity).getApplicationContext());
        TableRow.LayoutParams ikonicaParam = new TableRow.LayoutParams(40, 40);
        ikonica.setLayoutParams(ikonicaParam);
        ikonica.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(((MainActivity)mActivity).getApplicationContext()).load(R.drawable.comment).into(ikonica);
        desni.addView(ikonica);


        desni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }

        });

        ceo.addView(levi);
        ceo.addView(desni);
        textViewsHolder.addView(ceo);
        textViewsHolder.addView(textAddress);
        textViewsHolder.addView(textDescription);
        textViewsHolder.addView(textDistance);
        textViewsHolder.addView(textDelivery);

        restorauntLayout.addView(imageHolder);
        restorauntLayout.addView(textViewsHolder);

        return restorauntLayout;
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
}
