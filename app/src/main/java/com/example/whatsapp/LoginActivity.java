package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.logging.Logger;

public class LoginActivity extends AppCompatActivity {
    private Button LoginButton,LoginPhoneButton;
    private EditText Email,Password;
    private TextView ForgetPassword,NeedNewAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        InitializeFields();
        NeedNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });
    }

    private void AllowUserToLogin()
    {
     String email = Email.getText().toString();
     String password =Password.getText().toString();

     if (TextUtils.isEmpty(email))
     {
         Email.setError("Email Is Required");

     }
     if (TextUtils.isEmpty(password))
     {
         Password.setError("Password Is Required");

     }
     else
     {
         loadingBar.setTitle("Logging in");
         loadingBar.setMessage("Please Wait");
         loadingBar.setCanceledOnTouchOutside(false);
         loadingBar.show();
         mAuth.signInWithEmailAndPassword(email,password)
                 .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            SendUserToMainActivity();
                            Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            String message = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(LoginActivity.this, "Error : "+message, Toast.LENGTH_LONG).show();
                        }
                         loadingBar.dismiss();
                     }
                 });
     }
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void InitializeFields() {
        LoginButton =(Button) findViewById(R.id.login_button);
        LoginPhoneButton = (Button) findViewById(R.id.login_button_phone);
        Email = (EditText) findViewById(R.id.login_email);
        Password = (EditText) findViewById(R.id.login_password);
        ForgetPassword = (TextView) findViewById(R.id.forget_password_link_login);
        NeedNewAccount = (TextView) findViewById(R.id.login_create_new_account_text);
        loadingBar = new ProgressDialog(this);
    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}