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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Orders extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public Orders() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Orders.
     */
    // TODO: Rename and change types and number of parameters
    public static Orders newInstance(String param1, String param2) {
        Orders fragment = new Orders();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        final TableLayout ordersTable = (TableLayout)view.findViewById(R.id.ordersTable);


        ordersTable.setStretchAllColumns(true);
        ordersTable.bringToFront();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //check if user with this email exists
        db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                Log.e("settingsError", "Ne postoje orderi");
                            }else{
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();     //because only one user with this email should exist

                                for(DocumentSnapshot doc : documents){

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

                                }
                            }
                        } else {
                            Log.d("firebaseError", task.getException().toString());
                        }
                    }
                });

        return view;
    }


}
