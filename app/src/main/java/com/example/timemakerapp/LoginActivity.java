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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.timemakerapp.models.Progress;
import com.example.timemakerapp.models.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;


public class LoginActivity extends AppCompatActivity {
    // UI Elements
    EditText t_email;
    EditText t_password;
    Button bt_login;
    ImageButton bt_google;
    ImageButton bt_facebook;
    TextView t_forgotPassword;
    TextView t_navRegister;
    EditText t_recoverPassEmail;
    Button bt_sendEmail;
    TextView t_errorSendEmail;
    TextView t_errorLogin;
    ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGNGOOGLE_IN = 9001;
    private String TAG = "LoginActivity";
    private CallbackManager mCallbackManager;

    //achievement initialization
    //private AchievementDBupdater Achieveupdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mAuth = FirebaseAuth.getInstance();


        setFacebookCB();

        // Init
        t_email = findViewById(R.id.i_email);
        t_password = findViewById(R.id.i_password);
        bt_login = findViewById(R.id.bt_login);
        bt_google = findViewById(R.id.bt_google);
        bt_facebook = findViewById(R.id.bt_facebook);
        t_forgotPassword = findViewById(R.id.t_forgotPassword);
        t_navRegister = findViewById(R.id.t_navRegister);
        bt_sendEmail = findViewById(R.id.bt_sendEmail);
        t_recoverPassEmail = findViewById(R.id.t_recoverPassEmail);
        t_errorSendEmail = findViewById(R.id.t_errorSendEmail);
        t_errorLogin = findViewById(R.id.t_errorLogin);
        progressBar = findViewById(R.id.progressBar);

        // Set Listeners
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignInEmail(t_email.getText().toString(), t_password.getText().toString());
            }
        });
        bt_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignInGoogle();
            }
        });
        bt_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
            }
        });
        t_navRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
        t_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.forgotPassLayout).setVisibility(View.VISIBLE);
            }
        });
        bt_sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = t_recoverPassEmail.getText().toString();
                forgottenPassword(email);
            }
        });
    }

    // ############ EMAIL ############

    private void onSignInEmail(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Sing in existing users
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        closeKeyboard();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredentialEmail:success");
                            t_errorLogin.setVisibility(View.GONE);
                            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(loginIntent);
                            // FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredentialEmail:failed");
                            t_errorLogin.setText(task.getException().getMessage());
                            t_errorLogin.setVisibility(View.VISIBLE);
                        }
                        progressBar.setVisibility(View.GONE);


                    }
                });
    }

    // ############ GOOGLE ############
    private void onSignInGoogle() {
        progressBar.setVisibility(View.VISIBLE);

        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGNGOOGLE_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGNGOOGLE_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInGoogleResult(task);
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void handleSignInGoogleResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    closeKeyboard();
                    Toast.makeText(LoginActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    //FirebaseUser user = mAuth.getCurrentUser();
                    //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext()); // account.getDisplayName)=;
                    // Insert in database if new user
                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                        insertNewUser();

                    }
                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                } else {
                    closeKeyboard();
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);

            }
        });
    }


    // ############ FACEBOOK ############
    private void setFacebookCB() {

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_LONG).show();
                        firebaseAuthWithFacebook(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                insertNewUser();
                            }
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    public void insertNewUser() {

        System.out.println("Sign Up Completed");

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


                } else {
                    Log.d(TAG, "User failed insertion");
                }
            }
        });
        //insert new achievement entry with user
     //   AchievementDBupdater Achieveupdater = new AchievementDBupdater();
     //   Achieveupdater.insertNewAchieveEntry();
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

    private void forgottenPassword(String email) {
        Log.d(TAG, "THIS IS A TEST");

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        closeKeyboard();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            findViewById(R.id.forgotPassLayout).setVisibility(View.GONE);
                            t_errorSendEmail.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Email sent",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            t_errorSendEmail.setText(task.getException().getMessage());
                            t_errorSendEmail.setVisibility(View.VISIBLE);
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