package com.example.doandidong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView navView;
    private String userID,name,phone,email;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn thêm lần nữa để thoát", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent=getIntent();
        userID=intent.getStringExtra("userID");
        phone=intent.getStringExtra("phone");
        name=intent.getStringExtra("name");
        email=intent.getStringExtra("email");

        navView = findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(navListener);

        navView.getMenu().findItem(R.id.nav_home).setChecked(true);

        if(savedInstanceState == null){
            loadFragment(new HomeFragment());
        }
        //I added this if statement to keep the selected fragment when rotating the device
        navView.setOnNavigationItemSelectedListener(navListener);

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_notification:
                            selectedFragment = new NotificationFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                    loadFragment(selectedFragment);
                    return true;
                }
            };
    private void loadFragment(Fragment fragment){
        Bundle bundle=new Bundle();
        bundle.putString("userID",userID);
        bundle.putString("phone",phone);
        bundle.putString("name",name);
        bundle.putString("email",email);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment,fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

}