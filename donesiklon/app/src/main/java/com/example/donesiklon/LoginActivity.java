package com.example.donesiklon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b!= null) {
            if (b.containsKey("naslov"))
                Log.d("tag",i.getStringExtra("naslov"));
        }
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Button signInButton = (Button) findViewById(R.id.buttonSignIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Uzimanje podataka
                EditText username = (EditText) findViewById(R.id.usernameSignIn);
                EditText password = (EditText) findViewById(R.id.passwordSignIn);

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
        });

        Button signUpButton = (Button) findViewById(R.id.buttonSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

    }

}
