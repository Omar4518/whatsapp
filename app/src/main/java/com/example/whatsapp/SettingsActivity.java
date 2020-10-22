package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button updateButton;
    private EditText userName,userStatues;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        InitializeFields();




        mAuth = FirebaseAuth.getInstance();
        currentUserId= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();
    }




    private void InitializeFields()
    {
        updateButton = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name_settings);
        userStatues = (EditText) findViewById(R.id.set_user_statues_settings);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image_settings);

    }
    private void UpdateSettings()
    {
        String setUserName = userName.getText().toString();
        String setStatues = userStatues.getText().toString();

        if (TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this, "Please Write Your User Name", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setStatues))
        {
            Toast.makeText(this, "Please Write Your Statues", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap <String,String> profileMap = new HashMap<>();
            profileMap.put("name",setUserName);
            profileMap.put("statues",setStatues);
            RootRef.child("Users Profile").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                String message = Objects.requireNonNull(task.getException()).toString();
                                Toast.makeText(SettingsActivity.this, "Error: "+message, Toast.LENGTH_LONG).show();
                            }


                        }
                    });
        }

    }
    private void RetrieveUserInfo()
    {
        RootRef.child("Users Profile").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if ((snapshot.exists()) && (snapshot.hasChild("name") && (snapshot.hasChild("image"))))
                        {
                            String retrieveUserName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                            String retrieveUserStatues = Objects.requireNonNull(snapshot.child("statues").getValue()).toString();
                            String retrieveProfileImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();

                            userName.setText(retrieveUserName);
                            userStatues.setText(retrieveUserStatues);

                        }
                        else if((snapshot.exists()) && (snapshot.hasChild("name")))
                        {
                            String retrieveUserName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                            String retrieveUserStatues = Objects.requireNonNull(snapshot.child("statues").getValue()).toString();

                            userName.setText(retrieveUserName);
                            userStatues.setText(retrieveUserStatues);
                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this, "Please set your profile info", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}