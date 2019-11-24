package com.example.timemakerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button login;
    ImageButton google;
    ImageButton facebook;
    TextView forgotPassword;
    TextView navRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Init
        email = (EditText)findViewById(R.id.i_email);
        password = (EditText)findViewById(R.id.i_password);
        login = (Button)findViewById(R.id.bt_login);
        google = (ImageButton) findViewById(R.id.bt_google);
        facebook = (ImageButton) findViewById(R.id.bt_facebook);
        forgotPassword = (TextView)findViewById(R.id.t_forgotPassword);
        navRegister = (TextView)findViewById(R.id.t_navRegister);

        // Set Listeners
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
        });
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
        });
        navRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }
}
