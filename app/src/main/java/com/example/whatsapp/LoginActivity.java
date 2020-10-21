package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private static final int RC_SIGN_IN = 101;
    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView ForgetPasswordLink, CreateNewAccountLink;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton ButtonGoogle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitializeFields();//bandah 3la elmethod eli b3rf fiha eli mogod feldesign
        mAuth = FirebaseAuth.getInstance();
        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });
        //google

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        ButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.login_button_user_google) {
                    signIn();
                    // ...
                }
            }
        });
        CreateNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterUserActivity();
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {//zorar eloogin bandah fe 3almethod eli bt3ml login
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null)
        {
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        }
    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            Toast.makeText(LoginActivity.this, user.getEmail() + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
    /////////////////////////////////////////////////////////////////////////////google//////////////////////


    private void SendUserToRegisterUserActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void AllowUserToLogin()//elmetod eli bt3ml login
    {
        String userEmail = UserEmail.getText().toString();
        String userPassword = UserPassword.getText().toString();

        if (userEmail.isEmpty())//lw sab mkan elemail fady y2olo ektb email
        {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        } else if (userPassword.isEmpty())//lw sab mkan elpass fady y2olo ektb elpass
        {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (!userEmail.endsWith(".com") || !userEmail.contains("@")) {
            Toast.makeText(getApplicationContext(), "please valid E-mail", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("please wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {//lw 3aml elaccount mogod ytl3lo massg eno 3aml login
                                SendUserToMainUserActivity();
                                Toast.makeText(LoginActivity.this, "Logged in successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String message = Objects.requireNonNull(task.getException()).toString();//lw fe error ytl3lo elerror
                                Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }

                        }
                    });
        }
    }


    private void SendUserToMainUserActivity() {
        Intent mainUserIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainUserIntent);
    }


    private void InitializeFields() {//t3ref eli mogod feldesign
        LoginButton = (Button) findViewById(R.id.login_button_user);
        ButtonGoogle = (SignInButton) findViewById(R.id.login_button_user_google);
        CreateNewAccountLink = (TextView) findViewById(R.id.login_create_new_account_text_user);
        UserEmail = (EditText) findViewById(R.id.login_email_user);
        UserPassword= (EditText) findViewById(R.id.login_password_user);
        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link_login_user);
        loadingBar = new ProgressDialog(this);
    }

}