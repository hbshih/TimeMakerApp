package com.example.timemakerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText t_email;
    EditText t_password;
    EditText t_password2;
    Button bt_register;
    TextView t_navLogin;
    TextView t_errorRegister;
    private String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Init
        t_email = (EditText) findViewById(R.id.i_email);
        t_password = (EditText) findViewById(R.id.i_password);
        t_password2 = (EditText) findViewById(R.id.i_password2);
        bt_register = (Button) findViewById(R.id.bt_register);
        t_navLogin = (TextView) findViewById(R.id.t_navLogin);
        t_errorRegister = (TextView) findViewById(R.id.t_errorRegister);

        bt_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String auxPass = t_password.getText().toString();
                String auxPass2 = t_password2.getText().toString();

                if (auxPass.equals(auxPass2)) {
                    t_errorRegister.setVisibility(View.VISIBLE);
                    signUpUser(t_email.getText().toString(), auxPass);
                } else {
                    t_errorRegister.setText("Passwords do not match");
                    t_errorRegister.setVisibility(View.VISIBLE);
                }
            }
        });

        t_navLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    private void signUpUser(String email, String password) {

        // Itilialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            closeKeyboard();
                            t_errorRegister.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Successfuly Register", Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(loginIntent);
                        } else {
                            closeKeyboard();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            t_errorRegister.setText(task.getException().getMessage());
                            t_errorRegister.setVisibility(View.VISIBLE);
                        }

                        // ...
                    }
                });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
