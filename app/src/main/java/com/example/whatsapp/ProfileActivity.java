package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity
{

    private String receiverUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receiverUserId = getIntent().getExtras().get("visitUserId").toString();
        Toast.makeText(this, "User Id " + receiverUserId, Toast.LENGTH_LONG).show();

    }
}