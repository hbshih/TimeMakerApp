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

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    // UI Elements
    EditText t_email;
    EditText t_password;
    Button bt_login;
    ImageButton bt_google;
    ImageButton bt_facebook;
    TextView t_forgotPassword;
    TextView t_navRegister;
    // Firebase
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGNGOOGLE_IN = 9001;
    private String TAG = "LoginActivity";
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Hide Keyboad
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
    }

    // ############ EMAIL ############

    private void onSignInEmail(String email, String password) {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Sing in existing users
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "signInWithCredentialEmail:success");
                            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(loginIntent);
                            // FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.d(TAG, "signInWithCredentialEmail:failed");
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // ############ GOOGLE ############
    private void onSignInGoogle() {
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
        Log.d(TAG, "REQUESTCODE" + requestCode);

        if (requestCode == RC_SIGNGOOGLE_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInGoogleResult(task);
        } mCallbackManager.onActivityResult(requestCode, resultCode, data);

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
                    if (task.getResult().getAdditionalUserInfo().isNewUser()){
                        insertNewUser();
                    }
                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                } else {
                    closeKeyboard();
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // ############ FACEBOOK ############
    private void setFacebookCB(){
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
                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                insertNewUser();
                            }
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }


    public void insertNewUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference newUserRef = db.collection("users").document();
        User user = new User();
        user.uid = mAuth.getCurrentUser().getUid();
        user.email = mAuth.getCurrentUser().getEmail();

        Progress progress = new Progress();
        progress.setInitProgress();
        user.progress = progress;

        newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "User succesfully inserted");
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