package com.example.servicesondemand.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.User;

public class MainActivity extends Activity {
    private static int SPLASH_TIME_OUT = 1900;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Session session = new Session(getApplicationContext());
                User user = session.getUser();
                if(user == null){
                    Intent intent = new Intent(MainActivity.this, GetStarted.class);
                    startActivity(intent);
                }
                else if (user.getType() == 0) {
                    Intent intent = new Intent(MainActivity.this, CustomerDashboard.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, VendorDashboard.class);
                    startActivity(intent);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
