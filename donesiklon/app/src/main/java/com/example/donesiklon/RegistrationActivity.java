package com.example.donesiklon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity  extends AppCompatActivity {
    Button signUpButton;
    Button signInButton;
    EditText password;
    EditText username;
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
        username = (EditText)findViewById(R.id.username);
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
                    Log.i("uspeloRegistrovanje", "Da");
                    // Ide u bazu

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.successfullyReg), Toast.LENGTH_LONG).show();
                    // Redirekcija na login formu
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    RegistrationActivity.this.startActivity(intent);
                    finish();
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

    void addEditTextListeners(){
        // Treba jos da se proveri da li postoji vec korisnik sa tim username
        username.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 0 || s.equals("")){
                    username.setError(getResources().getString(R.string.reqUsername));
                    correct = false;
                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                    username.setError(getResources().getString(R.string.mustBeEmailUsername));
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
