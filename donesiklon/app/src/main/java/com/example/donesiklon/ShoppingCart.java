package com.example.donesiklon;

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
import com.example.donesiklon.model.Purchase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart extends Fragment {
    int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    int height = Resources.getSystem().getDisplayMetrics().heightPixels;
    ArrayList<Product> products = new ArrayList<Product>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart, container, false);
        final LinearLayout layout = view.findViewById(R.id.cart_items);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("purchases").whereEqualTo("status", "inCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size() == 0) {
                        layout.addView(createEmptyMenuLayout());
                    }else {
                        for (QueryDocumentSnapshot d : task.getResult()) {

                            Log.i("DDD", d.toString());
                            //db.collection("products").document(d.get("productId").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            db.collection("products").whereEqualTo("code",d.get("code").toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                           // Log.d(TAG, document.getId() + " => " + document.getData());
                                            Product product = createProduct(document.getId(),document.getData());
//                                            Product product = new Product();
//                                            product.setCode("ISqoPyzHRhneRIaUOryT");
//                                            product.setDescription("Probajte, ukusno je");
//                                            product.setImageUrl("https://firebasestorage.googleapis.com/v0/b/donesi-klon-firebase.appspot.com/o/chinese2.jpg?alt=media&token=8ca870ae-1f99-4184-9de6-5799bb20aacb");
//                                            product.setName("Kineska piletina");
//                                            product.setPrice(250);
//                                            product.setRestaurantId("3xLrl1dVId1mnJo37psz");
                                            products.add(product);
                                            layout.addView(createRestorauntMenuLayout(product));
                                        }


                                    }
                                }
                            });
                        }
                    }
                } else {
                    Log.d("qwe", "Error getting documents: ", task.getException());
                }
            }
        });

        Button myButton = new Button(getActivity().getApplicationContext());
        myButton.setText(R.string.checkout);
        myButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> data = new HashMap<>();


                db.collection("purchases").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Purchase purchase = document.toObject(Purchase.class);
                                purchase.setStatus("finished"); //Use the setter
                                String id = document.getId();
                                db.collection("purchases").document(id).set(purchase);
                            }

//                            Fragment currentFragment = getFragmentManager().findFragmentByTag("cart_items");
//                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                            fragmentTransaction.detach(currentFragment);
//                            fragmentTransaction.attach(currentFragment);
//                            fragmentTransaction.commit();

                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, new ShoppingCart(), "cart").commit();

//after transaction you must call the executePendingTransaction
                            getFragmentManager().executePendingTransactions();

//now you can get fragment which is added with tag
                            ShoppingCart exampleFragment = (ShoppingCart) getFragmentManager().findFragmentByTag("cart");


                            Toast.makeText(getActivity().getApplicationContext(), R.string.successfullyOrdered, Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });

        layout.addView(myButton);

        return view;
    }

    private Product createProduct(DocumentSnapshot document) {
        Product retVal = new Product();
        retVal.setId(document.getId());
        retVal.setCode(document.get("code").toString());
        retVal.setName(document.get("name").toString());
        retVal.setPrice(Integer.parseInt(document.get("price").toString()));
        retVal.setRestaurantId(document.get("restaurantId").toString());
        retVal.setDescription(document.get("description").toString());
        retVal.setImageUrl(document.get("imageUrl").toString());
        return retVal;
    }

    private Product createProduct(String id, java.util.Map<String,Object> data) {
        Product retVal = new Product();
        retVal.setId(id);
        retVal.setCode(data.get("code").toString());
        retVal.setName(data.get("name").toString());
        retVal.setPrice(Integer.parseInt(data.get("price").toString()));
        retVal.setRestaurantId(data.get("restaurantId").toString());
        retVal.setDescription(data.get("description").toString());
        retVal.setImageUrl(data.get("imageUrl").toString());
        return retVal;
    }

    private LinearLayout createRestorauntMenuLayout(Product product) {
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
        textDescriptionForProduct.setText("Opis: " + product.getDescription());

        LinearLayout priceHolder = new LinearLayout(getActivity().getApplicationContext());
        LinearLayout.LayoutParams layoutParamsPriceHolderLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParamsPriceHolderLayout.setMargins(25, 10, 25, 0);
        priceHolder.setOrientation(LinearLayout.VERTICAL);
        priceHolder.setLayoutParams(layoutParamsContentHolderLayout);
        priceHolder.setGravity(Gravity.BOTTOM);

        TextView textPrice = new TextView(getActivity().getApplicationContext());
        textPrice.setTextSize(13);
        textPrice.setTextColor(Color.rgb(130,2,2));
        textPrice.setText("Cena: " + product.getPrice());
        textPrice.setGravity(Gravity.RIGHT);
        priceHolder.addView(textPrice);

        textViewsHolder.addView(textName);
        textViewsHolder.addView(textDescriptionForProduct);
        textViewsHolder.addView(priceHolder);
        
        restorauntLayout.addView(imageHolder);
        restorauntLayout.addView(textViewsHolder);

        return restorauntLayout;
    }

    private LinearLayout createEmptyMenuLayout() {
        LinearLayout retValLayout = new LinearLayout(getActivity().getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.MATCH_PARENT);
        retValLayout.setLayoutParams(layoutParams);
        TextView message = new TextView(getActivity().getApplicationContext());
        message.setTextSize(20);
        message.setTypeface(null, Typeface.BOLD);
        message.setText(R.string.emptyCart);
        message.setGravity(Gravity.CENTER);
        retValLayout.addView(message);
        return retValLayout;
    }

}
