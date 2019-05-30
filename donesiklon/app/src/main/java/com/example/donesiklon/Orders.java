package com.example.donesiklon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.donesiklon.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Orders extends Fragment {
    String user;
    String userId;
    DocumentSnapshot d;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        user="";
        if(SaveSharedPreference.getUserName(getContext()).length() == 0)
        {
            Log.i("USER_SETTINGS", "ne");
            // Ako ne postoji VRATI GA NA FORMU ZA LOGOVANJE
        }
        else
        {
            Log.i("USER_SETTINGS", "da");
            user = SaveSharedPreference.getUserName(getContext());
            Log.i("ZAKACEN_USER", user);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        final TableLayout ordersTable = (TableLayout)view.findViewById(R.id.ordersTable);


        ordersTable.setStretchAllColumns(true);
        ordersTable.bringToFront();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //check if user with this email exists
        db.collection("users")
                .whereEqualTo("email", user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                Log.e("settingsError", "Ne postoji korisnik u bazi");
                            }else{
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);     //because only one user with this email should exist
                                userId = document.getId();
                                Log.i("ulogovanUBazi", userId);
                            }
                        } else {
                            Log.d("firebaseError", task.getException().toString());
                        }
                    }
                });



            db.collection("purchases").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.i("Order:", "1");
                    if (task.isSuccessful()) {
                        Log.i("Order:", "3");

                        ordersTable.removeAllViews();

                        for (QueryDocumentSnapshot document : task.getResult()) {


                            if (document.get("status").equals("finished")) {
                                Log.i("Order:", "4");
                                final String code = document.get("code").toString();

                                db.collection("products")
                                        .whereEqualTo("code", code).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                Log.i("Order:", "5");
                                                if (task.isSuccessful()) {
                                                    Log.i("Order:", "6");

                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (code.equals(document.get("code"))) {
                                                            TableRow tr = new TableRow(getContext());
                                                            TextView c1 = new TextView(getContext());
                                                            c1.setText(document.get("name").toString());
                                                            TextView c2 = new TextView(getContext());
                                                            c2.setText(String.valueOf(document.get("price")));
                                                            TextView c3 = new TextView(getContext());

                                                            tr.addView(c1);
                                                            tr.addView(c2);
                                                       
                                                            ordersTable.addView(tr);
                                                        }
                                                    }

                                                }

                                            }
                                        });
                            }
                        }


                    }


//                                        db.collection("products")
//                                                .get()
//                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                        if (task.isSuccessful()) {
//                                                            if(task.getResult().isEmpty()){
//                                                                Log.e("productNema", "Ne postoji product");
//                                                            }else{
//                                                                List<DocumentSnapshot> documentProducts = task.getResult().getDocuments();     //because only one user with this email should exist
//
//                                                                for(final DocumentSnapshot docPro : documentProducts) {
//                                                                    String productId = docPro.getId();


//                                                                    db.collection("purchases").whereEqualTo("code",code).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                        @Override
//                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                            if(task.isSuccessful()) {
//                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
//
//                                                                                    if(document.get("status").equals("finished")) {
//                                                                                        TableRow tr = new TableRow(getContext());
//                                                                                        TextView c1 = new TextView(getContext());
//                                                                                        c1.setText(docPro.get("name").toString());
//                                                                                        TextView c2 = new TextView(getContext());
//                                                                                        c2.setText(String.valueOf(docPro.get("price")))                                                                                        ;
//                                                                                        TextView c3 = new TextView(getContext());
//                                                                                        c3.setText(d.getData().get("status").toString());
//                                                                                        tr.addView(c1);
//                                                                                        tr.addView(c2);
//                                                                                        tr.addView(c3);
//                                                                                        ordersTable.addView(tr);
//                                                                                    }
//                                                                                }
//
//
//                                                                            }
//                                                                        }
//                                                                    });


//                                                                }}}
//
//
//                                                        else {
//                                                            Log.d("firebaseError", task.getException().toString());
//                                                        }
//                                                    }
//                                                });


                }


                                /*
                                for(DocumentSnapshot doc : documents){
                                    d = doc;
                                    db.collection("products")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if(task.getResult().isEmpty()){
                                                            Log.e("productNema", "Ne postoji product");
                                                        }else{
                                                            List<DocumentSnapshot> documentProducts = task.getResult().getDocuments();     //because only one user with this email should exist

                                                            for(DocumentSnapshot docPro : documentProducts) {
                                                                String productId = docPro.getId();
                                                                if(d.getData().get("productId").toString().equals(productId)){

                                                                    TableRow tr =  new TableRow(getContext());
                                                                    TextView c1 = new TextView(getContext());
                                                                    c1.setText(docPro.getData().get("name").toString());
                                                                    TextView c2 = new TextView(getContext());
                                                                    c2.setText(docPro.getData().get("price").toString());
                                                                    TextView c3 = new TextView(getContext());
                                                                    c3.setText(d.getData().get("status").toString());
                                                                    tr.addView(c1);
                                                                    tr.addView(c2);
                                                                    tr.addView(c3);
                                                                    ordersTable.addView(tr);
                                                                }
                                                            }

                                                        }
                                                    } else {
                                                        Log.d("firebaseError", task.getException().toString());
                                                    }
                                                }
                                            });


                                    TableRow tr =  new TableRow(getContext());
                                    TextView c1 = new TextView(getContext());
                                    c1.setText(doc.getData().get("firstName").toString());
                                    TextView c2 = new TextView(getContext());
                                    c2.setText(doc.getData().get("lastName").toString());
                                    TextView c3 = new TextView(getContext());
                                    c3.setText(doc.getData().get("phoneNumber").toString());
                                    tr.addView(c1);
                                    tr.addView(c2);
                                    tr.addView(c3);
                                    ordersTable.addView(tr);

                                }*/

//                        } else {
//                            Log.d("firebaseError", task.getException().toString());
//                        }
            });

        return view;
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

}
