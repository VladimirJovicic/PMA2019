package com.example.donesiklon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Settings extends Fragment {
    View view;
    EditText deliveryAddress;
    EditText password;
    EditText phoneNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.settings, container, false);

        String user="";
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
                                //wrongCredentials();
                            }else{
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);     //because only one user with this email should exist
                                String deliveryAddressFirebase = document.getData().get("deliveryAddress").toString();
                                String phoneNumberFirebase = document.getData().get("phoneNumber").toString();
                                String passwordFirebase = document.getData().get("password").toString();
                                Log.i("adresa", deliveryAddressFirebase);
                                Log.i("broj", phoneNumberFirebase);
                                Log.i("lozinka", passwordFirebase);

                                // Uzimanje
                                deliveryAddress = (EditText) view.findViewById(R.id.deliveryAddressSettings);
                                password = (EditText) view.findViewById(R.id.passwordSettings);
                                phoneNumber = (EditText) view.findViewById(R.id.phoneNumberSettings);

                                deliveryAddress.setText(deliveryAddressFirebase);
                                password.setText(passwordFirebase);
                                phoneNumber.setText(phoneNumberFirebase);

//phoneNumber passwordSettings deliveryAddressSettings
                               /* if(document.getData().get("password").equals(password.getText().toString())){
                                    //succesfulLogin();
                                }else{
                                    //wrongCredentials();
                                }*/
                            }
                        } else {
                            Log.d("firebaseError", task.getException().toString());
                           // Toast.makeText(getApplicationContext(), "Error getting documents for email checking", Toast.LENGTH_LONG).show();
                        }
                    }
                });







        //return inflater.inflate(R.layout.settings, container, false);
        return view;
    }

}
