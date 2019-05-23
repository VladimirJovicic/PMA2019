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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button signInButton;
    Button signUpButton;
    EditText username;
    EditText password;
    boolean correct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        username = (EditText) findViewById(R.id.usernameSignIn);
        password = (EditText) findViewById(R.id.passwordSignIn);

        addEditTextListeners();

        signInButton = (Button) findViewById(R.id.buttonSignIn);
        signUpButton = (Button) findViewById(R.id.buttonSignUp);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!correct){
                    Log.i("uspeloLogovanje", "Ne");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.reqAll), Toast.LENGTH_LONG).show();
                }
                else{
                    Log.i("uspeloLogovanje", "Da");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.successfullyLogged), Toast.LENGTH_LONG).show();
                    // Uzimanje podataka

                    Log.i("USERNAME_LOGOVANOG", username.getText().toString());
                    Log.i("PASSWORD_LOGOVANOG", password.getText().toString());

                    //Ovde ce ici provera sa bazom da li postoji to ime i sifra
                    Log.i("USPESNO_LOGOVANJE", "da");
                    // Treba da se sacuva u SharedPreference
                    SaveSharedPreference.setUserName(LoginActivity.this,username.getText().toString());

                    /*
                    if(username.getText().toString().equals("olja") && password.getText().toString().equals("olja")){
                        Log.i("USPESNO_LOGOVANJE", "da");
                        // Treba da se sacuva u SharedPreference
                        SaveSharedPreference.setUserName(LoginActivity.this,username.getText().toString());
                    }
                    else{
                        Log.i("USPESNO_LOGOVANJE", "ne");
                    }
                     */

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(intent);
                    //da back iz main activitya ne vrati na login
                    finish();
                }
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

    }

    void addEditTextListeners(){
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
                else{
                    correct = true;
                }
            }
        });

    }

}
