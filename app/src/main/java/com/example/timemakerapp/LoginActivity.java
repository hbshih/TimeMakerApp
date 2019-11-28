package com.example.timemakerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    // Firebase
    private FirebaseAuth mAuth;

    // UI Elements
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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
                onSingIn(email.getText().toString(), password.getText().toString());
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

    private void onSingIn(String email, String password) {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Sing in existing users
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(loginIntent);
                            // FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}
