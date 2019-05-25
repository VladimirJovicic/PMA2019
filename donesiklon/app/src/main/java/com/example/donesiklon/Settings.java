package com.example.donesiklon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
    Button buttonChangeDeliveryAddress;
    String user;
    String userId;
    boolean correct = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.settings, container, false);

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
                                String deliveryAddressFirebase = document.getData().get("deliveryAddress").toString();
                                String phoneNumberFirebase = document.getData().get("phoneNumber").toString();
                                String passwordFirebase = document.getData().get("password").toString();
                                /*
                                Log.i("adresa", deliveryAddressFirebase);
                                Log.i("broj", phoneNumberFirebase);
                                Log.i("lozinka", passwordFirebase);
                                */
                                // Uzimanje polja sa fronta i popunjavanje vrednostima iz baze
                                deliveryAddress = (EditText) view.findViewById(R.id.deliveryAddressSettings);
                                password = (EditText) view.findViewById(R.id.passwordSettings);
                                phoneNumber = (EditText) view.findViewById(R.id.phoneNumberSettings);

                                deliveryAddress.setText(deliveryAddressFirebase);
                                password.setText(passwordFirebase);
                                phoneNumber.setText(phoneNumberFirebase);
                                addEditTextListeners();
                            }
                        } else {
                            Log.d("firebaseError", task.getException().toString());
                        }
                    }
                });


        buttonChangeDeliveryAddress = (Button) view.findViewById(R.id.buttonChangeDeliveryAddress);
        buttonChangeDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  addEditTextListeners();

                if(correct) {
                    Log.i("PODESAVANJA","promena");
                    db.collection("users").document(userId).update("deliveryAddress", deliveryAddress.getText().toString(), "phoneNumber", phoneNumber.getText().toString(), "password", password.getText().toString());
                }
            }
        });
        //return inflater.inflate(R.layout.settings, container, false);
        return view;
    }


    void addEditTextListeners(){
        deliveryAddress.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 0 || s.equals("")){
                    deliveryAddress.setError(getResources().getString(R.string.reqDeliveryAddress));
                    correct = false;
                }
                else{
                    correct = true;
                }
            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 0 || s.equals("")){
                    phoneNumber.setError(getResources().getString(R.string.reqPhoneNumber));
                    correct = false;
                }
                else{
                    correct = true;
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 0 || s.equals("")){
                    password.setError(getResources().getString(R.string.reqPassword));
                    correct = false;
                }else if(password.length()<6){
                    password.setError(getResources().getString(R.string.mustLengthPassword));
                    correct = false;
                }
                else{
                    correct = true;
                }
            }
        });

    }

}

