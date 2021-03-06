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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    Button signInButton;
    Button signUpButton;
    EditText email;
    EditText password;
    boolean correctMail = false;
    boolean correctPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        redirectIfUserAlreadyLogged();
        setContentView(R.layout.activity_signin);

        email = (EditText) findViewById(R.id.emailSignIn);
        password = (EditText) findViewById(R.id.passwordSignIn);

        addEditTextListeners();

        signInButton = (Button) findViewById(R.id.buttonSignIn);
        signUpButton = (Button) findViewById(R.id.buttonSignUp);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!correctPassword || !correctMail){
                    Log.i("uspeloLogovanje", "Ne");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.reqAll), Toast.LENGTH_LONG).show();
                }
                else{
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    //check if user with this email exists
                    db.collection("users")
                            .whereEqualTo("email", email.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().isEmpty()){
                                            wrongCredentials();
                                        }else{
                                            DocumentSnapshot document = task.getResult().getDocuments().get(0);     //because only one user with this email should exist
                                            if(document.getData().get("password").equals(password.getText().toString())){
                                                succesfulLogin(document.getId());
                                            }else{
                                                wrongCredentials();
                                            }
                                        }
                                    } else {
                                        Log.d("firebaseError", task.getException().toString());
                                        Toast.makeText(getApplicationContext(), R.string.errorGetting, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }

        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // clearTextFields();
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

    }

    private void redirectIfUserAlreadyLogged() {
        String user="";
        if(SaveSharedPreference.getUserName(LoginActivity.this).length() == 0)
        {
            Log.i("DA_LI_POSTOJI_USER", "ne");
            // Ako ne postoji VRATI GA NA FORMU ZA LOGOVANJE
        }
        else
        {
            Log.i("DA_LI_POSTOJI_USER", "da");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(intent);
            //da back iz main activitya ne vrati na login
            finish();
        }
    }

    void clearTextFields(){
        email.setText("");
        password.setText("");
    }

    void succesfulLogin(String userId){
        Log.i("uspeloLogovanje", "Da");
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.successfullyLogged), Toast.LENGTH_LONG).show();

        // Treba da se sacuva u SharedPreference
        SaveSharedPreference.setUserName(LoginActivity.this, email.getText().toString());

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
        //da back iz main activitya ne vrati na login
        finish();
    }

    void wrongCredentials(){
        Toast.makeText(getApplicationContext(), R.string.wrongCredentials, Toast.LENGTH_LONG).show();
    }

    void addEditTextListeners(){
        email.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 0 || s.equals("")){
                    email.setError(getResources().getString(R.string.reqUsername));
                    correctMail = false;
                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                    email.setError(getResources().getString(R.string.mustBeEmailUsername));
                    correctMail = false;
                }
                else{
                    correctMail = true;
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
                    correctPassword = false;
                }
                else{
                    correctPassword = true;
                }
            }
        });

    }

}
