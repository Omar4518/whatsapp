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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.InstallationTokenResult;

import java.util.Objects;

public class  RegisterActivity extends AppCompatActivity {
    private EditText Email,Password;
    private Button RegisterButton;
    private TextView AlreadyHaveAccount;
    private FirebaseAuth registerAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRef;
    private FirebaseDatabase reference;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitializeFields();
        registerAuth= FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
        AlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
    }

    private void CreateNewAccount()
    {
        String email = Email.getText().toString();
        String password = Password.getText().toString();
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
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            registerAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();






                                reference = FirebaseDatabase.getInstance();
                                RootRef = reference.getReference("Users Profile");

                                RootRef.child("Users Profile").child(currentUserId).child("device_token").setValue(deviceToken);
                                String email = Email.getEditableText().toString();
                                String password = Password.getEditableText().toString();
                                UserHelper userHelper = new UserHelper(email,password);
                                SendUserToMainActivity();
                                RootRef.child(currentUserId).setValue(userHelper);
                                Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
                            }
                            else
                                {
                                    String message = Objects.requireNonNull(task.getException()).toString();
                                    Toast.makeText(RegisterActivity.this, "Error : "+message, Toast.LENGTH_LONG).show();

                                }
                            loadingBar.dismiss();

                        }
                    });
        }
    }




    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    private void InitializeFields()
    {
        Email = (EditText) findViewById(R.id.edit_text_email_register);
        Password = (EditText) findViewById(R.id.edit_text_password_register);
        RegisterButton = (Button) findViewById(R.id.button_register);
        AlreadyHaveAccount = (TextView) findViewById(R.id.already_have_account);
        loadingBar = new ProgressDialog(this);
    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}