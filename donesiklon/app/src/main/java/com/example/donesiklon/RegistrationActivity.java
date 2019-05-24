package com.example.donesiklon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.donesiklon.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class RegistrationActivity  extends AppCompatActivity {
    Button signUpButton;
    Button signInButton;
    EditText password;
    EditText email;
    EditText name;
    EditText surname;
    EditText deliveryAddress;
    EditText phoneNumber;
    boolean correct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Datas
        password = (EditText)findViewById(R.id.password);
        email = (EditText)findViewById(R.id.email);
        name = (EditText)findViewById(R.id.name);
        surname = (EditText)findViewById(R.id.surname);
        deliveryAddress = (EditText)findViewById(R.id.deliveryAddress);
        phoneNumber = (EditText)findViewById(R.id.phoneNumber);

        addEditTextListeners();

        signUpButton = (Button) findViewById(R.id.registrationButtonSignUp);
        signInButton = (Button) findViewById(R.id.registrationButtonSignIn);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!correct){
                    Log.i("uspeloRegistrovanje", "Ne");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.reqAll), Toast.LENGTH_LONG).show();
                }
                else{
                    // Ide u bazu
                    saveUserToDatabase(email.getText().toString(), password.getText().toString(),
                            name.getText().toString(), surname.getText().toString(), deliveryAddress.getText().toString(),
                            phoneNumber.getText().toString());
                }
            }

        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("prebacivanjeNaPrijavu", "Da");

                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                RegistrationActivity.this.startActivity(intent);
                finish();
            }
        });
    }

    void successfulRegistration(){
        Log.i("uspeloRegistrovanje", "Da");
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.successfullyReg), Toast.LENGTH_LONG).show();
        // Redirekcija na login formu
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        RegistrationActivity.this.startActivity(intent);
        finish();
    }

    void failedRegistration(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    void saveUserToDatabase(String email, String password, String firstName, String lastName, String deliveryAddres, String phoneNumber){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final User user = new User(email, password, firstName, lastName, deliveryAddres, phoneNumber);

        //check if email exists
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                //if email doesnt exist insert new user
                                insertUserToDatabase(db, user);
                            }else{
                                failedRegistration("Email you entered is taken");
                            }
                        } else {
                            Log.d("firebaseError", task.getException().toString());
                            failedRegistration("Error getting documents for email checking");
                        }
                    }
                });
    }

    void insertUserToDatabase(FirebaseFirestore db, User user){
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        successfulRegistration();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("firebaseError", e.toString());
                        failedRegistration("Couldn't add user to database");
                    }
                });
    }

    void addEditTextListeners(){
        // Treba jos da se proveri da li postoji vec korisnik sa tim username
        email.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 0 || s.equals("")){
                    email.setError(getResources().getString(R.string.reqUsername));
                    correct = false;
                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                    email.setError(getResources().getString(R.string.mustBeEmailUsername));
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
                }
                else if(password.length()<6){
                    password.setError(getResources().getString(R.string.mustLengthPassword));
                    correct = false;
                }
                else{
                    correct = true;
                }
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 0 || s.equals("")){
                    name.setError(getResources().getString(R.string.reqName));
                    correct = false;
                }
                else{
                    correct = true;
                }
            }
        });

        surname.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 0 || s.equals("")){
                    surname.setError(getResources().getString(R.string.reqSurname));
                    correct = false;
                }
                else{
                    correct = true;
                }
            }
        });

        deliveryAddress.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 0 || s.toString().equals("")){
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
    }
}
