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

import com.example.timemakerapp.models.Progress;
import com.example.timemakerapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    EditText password2;
    Button register;
    TextView navLogin;
    private FirebaseAuth mAuth;
    private String TAG = "RegisterActivity: ";

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
                String auxPass = password.getText().toString();
                String auxPass2 = password2.getText().toString();

                if (auxPass.equals(auxPass2)) {
                    signUpUser(email.getText().toString(), auxPass);
                } else {
                    TextView matchError = (TextView) findViewById(R.id.t_matchError);
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

    private void signUpUser(String email, String password) {

        // Itilialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            closeKeyboard();
                            TextView matchError = (TextView) findViewById(R.id.t_matchError);
                            matchError.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Successfuly Register", Toast.LENGTH_SHORT).show();
                            createNewUser(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getEmail());
                        } else {
                            closeKeyboard();
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void createNewUser(String _uid, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = _uid;

        DocumentReference newUserRef = db.collection("users").document();
        User user = new User();
        user.uid = uid;
        user.email = email;

        Progress progress = new Progress();
        progress.setInitProgress();
        user.progress = progress;

        newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "User succesfully inserted");
                    Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                } else {
                    Log.d(TAG, "User failed insertion");

                }
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
