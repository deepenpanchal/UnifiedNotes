package com.flash.apps.noted;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class Login extends AppCompatActivity {

    private Button btnLogin;
    private Button gSignin;
    private TextView clickHere, forgotPassword;
    private EditText emailaddress;
    private EditText userPassword;
    private ProgressBar progressBar;
    private CheckBox rememberMe;

    private TextInputLayout txlemail;
    private TextInputLayout txlpassword;

    private FirebaseAuth firebaseAuth;
    private SharedPreferences loginRemember;
    private SharedPreferences.Editor loginRememberEditor;

    private boolean saveLogin;
    private final static int SIGNIN = 123;
    private static final String TAG = "";

    private EvernoteSession mEvernoteSession;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;

    private static final String CONSUMER_KEY = "deepenpanchal";
    private static final String CONSUMER_SECRET = "da85d51591b63a6d";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;

    private GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.login);
        gSignin = findViewById(R.id.gSignin);
        clickHere = findViewById(R.id.clickhere);
        forgotPassword = findViewById(R.id.forgotpassword);
        emailaddress = findViewById(R.id.emailaddress);
        userPassword = findViewById(R.id.password);
        txlemail = findViewById(R.id.text_input_layout);
        txlpassword = findViewById(R.id.text_input_layout2);
        progressBar = findViewById(R.id.progressBar);
        rememberMe = findViewById(R.id.rememberme);
        firebaseAuth = FirebaseAuth.getInstance();
        loginRemember = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginRememberEditor = loginRemember.edit();


        clickHere.setTextColor(ContextCompat.getColor(Login.this, R.color.colorPrimary));
        btnLogin.setTextColor(ContextCompat.getColor(Login.this, R.color.buttonText));

        if (firebaseAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(Login.this, CreateNote.class));
        }

        clickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgotPass.class));
            }
        });

        gSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        saveLogin = loginRemember.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            emailaddress.setText(loginRemember.getString("username", ""));
            userPassword.setText(loginRemember.getString("password", ""));
            rememberMe.setChecked(true);
        }

        //Creating Evernote Instance
        mEvernoteSession = new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(emailaddress.getWindowToken(), 0);

                final String email = emailaddress.getText().toString();
                String password = userPassword.getText().toString();

                if (rememberMe.isChecked()) {
                    loginRememberEditor.putBoolean("saveLogin", true);
                    loginRememberEditor.putString("username", email);
                    loginRememberEditor.putString("password", password);
                    loginRememberEditor.commit();
                } else {
                    loginRememberEditor.clear();
                    loginRememberEditor.commit();
                }

                //doSomethingElse();

                if(TextUtils.isEmpty(email))
                {
                    txlemail.setError("Email address is required");
                    return;
                }

                else
                {
                    txlemail.setError(null);
                }

                if(TextUtils.isEmpty(password))
                {
                    txlpassword.setError("Password can't be empty");
                }

                else
                {
                    txlpassword.setError(null);
                }

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.GONE);
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(Login.this, getString(R.string.authError), Toast.LENGTH_LONG).show();
                        }

                        else
                        {
                            Intent toDashboard = new Intent(Login.this, Dashboard.class);
                            toDashboard.putExtra("email", email);
                            startActivity(toDashboard);
                            finish();
                        }



                    }
                });

            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, SIGNIN);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGNIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(Login.this, CreateNote.class));
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Aut Fail", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


}

//For signout

/*
 mAuth = FirebaseAuth.getInstance();

         mAuthListner = new FirebaseAuth.AuthStateListener() {
@Override
public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser()==null)
        {
        startActivity(new Intent(Home_screen.this, singin_activity.class));
        }
        }
        };

        button.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        mAuth.signOut();


        }

        */