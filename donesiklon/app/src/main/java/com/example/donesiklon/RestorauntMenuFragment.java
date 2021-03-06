
package com.example.donesiklon;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.donesiklon.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RestorauntMenuFragment extends Fragment {
    private String naslov = "";
    private String id;

    int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    int height = Resources.getSystem().getDisplayMetrics().heightPixels;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        id = args.getString("id");

        View view = inflater.inflate(R.layout.restoraunt_menu, container, false);
        final LinearLayout layout = view.findViewById(R.id.meni_items);

        db.collection("products").
                whereEqualTo("restaurantId", id).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size() == 0) {
                        layout.addView(createEmptyMenuLayout());
                    }else {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("asd", document.get("name").toString());
                            Product product = createProduct(document);
                            layout.addView(createRestorauntMenuLayout(product));
                        }
                    }
                }else {
                    Log.d("qwe", "Error getting documents: ", task.getException());
                }
            }
        });
        return view;

    }

    private Product createProduct(QueryDocumentSnapshot document) {
        Product retVal = new Product();
        retVal.setId(document.getId());
        retVal.setCode(document.get("code").toString());
        retVal.setName(document.get("name").toString());
        String price = document.get("price").toString();
        retVal.setPrice(Double.parseDouble(price));
        retVal.setRestaurantId(document.get("restaurantId").toString());
        retVal.setDescription(document.get("description").toString());
        retVal.setImageUrl(document.get("imageUrl").toString());
        return  retVal;
    }

    private LinearLayout createRestorauntMenuLayout(final Product product) {
        LinearLayout restorauntLayout = new LinearLayout(getActivity().getApplicationContext());
        restorauntLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height/5+30);
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
        Glide.with(getActivity().getApplicationContext()).load(product.getImageUrl()).into(imageView);
        imageHolder.addView(imageView);

        ImageView image = new ImageView(getActivity().getApplicationContext());
        Glide.with(getActivity().getApplicationContext()).load(product.getImageUrl()).into(image);

        LinearLayout textViewsHolder = new LinearLayout(getActivity().getApplicationContext());
        LinearLayout.LayoutParams layoutParamsContentHolderLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsContentHolderLayout.setMargins(25, 10, 25, 0);
        textViewsHolder.setOrientation(LinearLayout.VERTICAL);
        textViewsHolder.setLayoutParams(layoutParamsContentHolderLayout);

        TextView textName = new TextView(getActivity().getApplicationContext());
        textName.setTextSize(20);
        textName.setTypeface(null, Typeface.BOLD);
        textName.setText(product.getName());

        TextView textDescriptionForProduct = new TextView(getActivity().getApplicationContext());
        textDescriptionForProduct.setTextSize(13);
        textDescriptionForProduct.setText(product.getDescription());

        LinearLayout priceHolder = new LinearLayout(getActivity().getApplicationContext());
        LinearLayout.LayoutParams layoutParamsPriceHolderLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParamsPriceHolderLayout.setMargins(25, 10, 25, 0);
        priceHolder.setOrientation(LinearLayout.VERTICAL);
        priceHolder.setLayoutParams(layoutParamsContentHolderLayout);
        priceHolder.setGravity(Gravity.BOTTOM);

        TextView textPrice = new TextView(getActivity().getApplicationContext());
        textPrice.setTextSize(13);
        textPrice.setTextColor(Color.rgb(130,2,2));
        textPrice.setText(Double.toString(product.getPrice()));
        textPrice.setGravity(Gravity.RIGHT);
        priceHolder.addView(textPrice);
        Button myButton = new Button(getActivity().getApplicationContext());
        myButton.setText(R.string.addToCart);
        myButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("productVan", product.toString());
                Map<String, Object> data = new HashMap<>();
                data.put("productId", product.getId());
                data.put("code", product.getCode());
                data.put("status", "inCart");
                data.put("userId", SaveSharedPreference.getUserName(((MainActivity)mActivity).getApplicationContext()));

                db.collection("purchases")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("productInner", product.toString());
                                Log.d("ADDOK", product.getName().toString());
                                Toast.makeText(getActivity().getApplicationContext(), R.string.addedToCart, Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("ADDERROR", "Error adding document", e);
                            }
                        });


            }
        });

        textName.setTextSize(20);
        textName.setTypeface(null, Typeface.BOLD);
        textName.setText(product.getName());

        textViewsHolder.addView(textName);
        textViewsHolder.addView(textDescriptionForProduct);
        textViewsHolder.addView(priceHolder);
        textViewsHolder.addView(myButton);

        restorauntLayout.addView(imageHolder);
        restorauntLayout.addView(textViewsHolder);

        return restorauntLayout;
    }

    private Activity mActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity = (Activity) context;
        }
    }

    private LinearLayout createEmptyMenuLayout() {
        LinearLayout retValLayout = new LinearLayout(getActivity().getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.MATCH_PARENT);
        retValLayout.setLayoutParams(layoutParams);
        TextView message = new TextView(getActivity().getApplicationContext());
        message.setTextSize(20);
        message.setTypeface(null, Typeface.BOLD);
        message.setText(R.string.emptyMenu);
        message.setGravity(Gravity.CENTER);
        retValLayout.addView(message);
        return retValLayout;
    }

    private void message() {
        Toast.makeText(this.getActivity(), R.string.meal + " " + naslov + " " + R.string.addedMeal , Toast.LENGTH_LONG).show();
    }
}

