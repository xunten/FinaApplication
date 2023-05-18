package com.example.finalapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.finalapplication.R;
import com.example.finalapplication.fragment.HomeFragment;
import com.example.finalapplication.fragment.ProfileFragment;
import com.example.finalapplication.fragment.SupportFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    LinearLayout homeBtn, profileBtn, supportBtn, productBtn;
    FloatingActionButton cartBtn;
    Fragment homeFragment, supportFragment, profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        loadFragment(homeFragment);

        initView();
        initControlFragment();
    }

    private void initView() {
        homeBtn = findViewById(R.id.homeBtn);
        profileBtn = findViewById(R.id.profileBtn);
        supportBtn = findViewById(R.id.supportBtn);
        productBtn = findViewById(R.id.productBtn);
        cartBtn = findViewById(R.id.cartBtn);
    }

    private void initControlFragment() {
        //Home button
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeFragment = new HomeFragment();
                loadFragment(homeFragment);
            }
        });

        //Profile button
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFragment = new ProfileFragment();
                loadFragment(profileFragment);
            }
        });

        //Support Button
        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFragment = new SupportFragment();
                loadFragment(supportFragment);
            }
        });

        //My Cart
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

        //All Products
        productBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ShowAllProductActivity.class));
            }
        });

    }

    private void loadFragment(Fragment homeFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, homeFragment);
        transaction.commit();
    }

}