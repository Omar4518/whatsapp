package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdabtor myTabsAccessorAdabtor;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WhatsApp");

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdabtor = new TabsAccessorAdabtor(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdabtor);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null)
        {
            SendUserToLoginActivity();
        }
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }
}