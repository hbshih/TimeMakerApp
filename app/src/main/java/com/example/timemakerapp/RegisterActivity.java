package com.example.timemakerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    EditText password2;
    Button register;
    TextView navLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Init
        email = (EditText) findViewById(R.id.i_email);
        password = (EditText) findViewById(R.id.i_password);
        password2 = (EditText) findViewById(R.id.i_password2);
        register = (Button) findViewById(R.id.bt_register);
        navLogin = (TextView) findViewById(R.id.t_navLogin);

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TextView matchError = (TextView) findViewById(R.id.t_matchError);
                if (password.getText().toString().equals(password2.getText().toString())) {
                    matchError.setVisibility(View.GONE);
                    Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                } else {
                    matchError.setVisibility(View.VISIBLE);
                }
            }
        });

        navLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(registerIntent);
            }
        });
    }
}
