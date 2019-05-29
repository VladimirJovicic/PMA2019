package com.example.donesiklon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


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

        db.collection("purchases")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                Log.e("orderError", "Ne postoje orderi");
                            }else{
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();     //because only one user with this email should exist
                                for(DocumentSnapshot doc : documents) {
                                    String uId =  doc.getData().get("userId").toString();
                                    if(uId.endsWith(userId)){
                                        Log.i("daLisuJednaki", "da");
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
                                                                    if(d.getData().get("productId").toString().endsWith(productId)){

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
                                                                }}}


                                                        else {
                                                            Log.d("firebaseError", task.getException().toString());
                                                        }
                                                    }
                                                });



                                    }

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
                            }
                        } else {
                            Log.d("firebaseError", task.getException().toString());
                        }
                    }
                });

        return view;
    }


}
