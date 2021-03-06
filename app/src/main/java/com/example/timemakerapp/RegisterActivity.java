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
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class RegisterActivity extends AppCompatActivity {
    EditText t_email;
    EditText t_password;
    EditText t_password2;
    Button bt_register;
    TextView t_navLogin;
    TextView t_errorRegister;
    ProgressBar progressBar;

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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        bt_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String auxPass = t_password.getText().toString();
                String auxPass2 = t_password2.getText().toString();

                if (auxPass.equals(auxPass2)) {
                    t_errorRegister.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
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
                            insertNewUser();
                        } else {
                            closeKeyboard();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            t_errorRegister.setText(task.getException().getMessage());
                            t_errorRegister.setVisibility(View.VISIBLE);
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    public void insertNewUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference newUserRef = db.collection("users").document();
        User user = new User();
        user.uid = mAuth.getCurrentUser().getUid();
        user.email = mAuth.getCurrentUser().getEmail().toLowerCase();

        Progress progress = new Progress();
        progress.setInitProgress();
        user.progress = progress;

        newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User succesfully inserted");
                    t_errorRegister.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Successfuly Register", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                } else {
                    Log.d(TAG, "User failed insertion");
                    t_errorRegister.setText(task.getException().getMessage());
                    t_errorRegister.setVisibility(View.VISIBLE);
                }
            }
        });
        addAchievementDB();
    }

    private void addAchievementDB()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create new user data
        Date today = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L);
        Map<String, Object> docData = new HashMap<>();
        docData.put("day_streak_3", 0);
        docData.put("day_streak_7", 0);
        docData.put("last_completed_goal_date", today);
        docData.put("number_of_completed_tasks", 0);
        db.collection("user_achievements").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(docData);
        System.out.println("Query Failed - Created new user");
        insertNewAchieveEntry(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void insertNewAchieveEntry(String currentUser){

        FirebaseFirestore.getInstance()
                .collection(
                        "achievements")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean NewUser = true;
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                Map<String,Object> taskMap = doc.getData();
                                Map<String,Object> order =  (Map<String,Object> )taskMap.get("order");
                                for (Map.Entry<String, Object> entry : order.entrySet()) {
                                    String k = entry.getKey();
                                    System.out.println("OnSuccess : " + currentUser);
                                    if(k.equals(currentUser)){
                                        NewUser = false;
                                        break;
                                    }
                                }
                                break;
                            }

                            if(NewUser){
                                Map<String, Object> newOrder = new HashMap<>();
                                Map<String, Object> newOrders = new HashMap<>();
                                newOrder.put(currentUser, 0);
                                newOrders.put("order", newOrder);
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    FirebaseFirestore.getInstance().collection("achievements").document(doc.getId()).set(newOrders, SetOptions.merge());
                                }
                            }
                        }
                        else System.out.println("Query Failed");

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