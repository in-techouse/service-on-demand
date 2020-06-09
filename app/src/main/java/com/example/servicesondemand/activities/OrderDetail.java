package com.example.servicesondemand.activities;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.daimajia.slider.library.SliderLayout;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.example.servicesondemand.R;

public class OrderDetail extends AppCompatActivity {
    private TextView date, time, offers, category, status, address, description;
    private Helpers helpers;
    private SliderLayout slider;
    private Session session;
    private ProgressDialog loadingBar;
    private Post post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        offers = findViewById(R.id.offers);
        category = findViewById(R.id.category);
        status = findViewById(R.id.status);
        address = findViewById(R.id.address);
        description = findViewById(R.id.description);

        date.setText(post.getDate());
        time.setText(post.getTime());
        offers.setText(post.getOffers() + "");
        category.setText(post.getCategory());
        status.setText(post.getStatus());
        address.setText(post.getAddress());
        description.setText(post.getDescription());

        loadingBar = new ProgressDialog(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
