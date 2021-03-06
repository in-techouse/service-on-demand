package com.example.servicesondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.model.Offer;
import com.example.servicesondemand.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfferVenderDetail extends AppCompatActivity {
    private static final String TAG = "OfferDetail";
    private Offer offer;
    private TextView name;
    private TextView phonenumber;
    private TextView email;
    private LinearLayout loading;
    private ScrollView main;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
    private ValueEventListener listener;
    private Helpers helpers;
    private User customer;
    private CircleImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_vender_detail);

        Intent it = getIntent();
        if (it == null) {
            Log.e(TAG, "Intent is null");
            finish();
            return;
        }

        Bundle bundle = it.getExtras();
        if (bundle == null) {
            Log.e(TAG, "Bundle is null");
            finish();
            return;
        }

        offer = (Offer) bundle.getSerializable("offer");
        if (offer == null) {
            Log.e(TAG, "Offer is null");
            finish();
            return;
        }

        TextView time = findViewById(R.id.time);
        TextView budget = findViewById(R.id.budget);
        TextView description = findViewById(R.id.description);
        loading = findViewById(R.id.loading);
        main = findViewById(R.id.main);
        helpers = new Helpers();
        name = findViewById(R.id.name);
        phonenumber = findViewById(R.id.phoneNumber);
        email = findViewById(R.id.email);
        image = findViewById(R.id.image);


        budget.setText(offer.getBudgetOffered() + "Rs");
        time.setText(offer.getTimeRequired() + "Minutes");
        description.setText(offer.getDescription());
        loadUserDetail();
    }

    private void loadUserDetail() {
        loading.setVisibility(View.VISIBLE);
        main.setVisibility(View.GONE);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listener != null)
                    reference.child(offer.getUserId()).removeEventListener(listener);
                if (dataSnapshot.exists()) {
                    customer = dataSnapshot.getValue(User.class);
                    if (customer != null) {
                        name.setText(customer.getFirstName() + " " + customer.getLastName());
                        phonenumber.setText(customer.getPhone());
                        email.setText(customer.getEmail());
                        if (customer.getImage() != null && customer.getImage().length() > 0) {
                            Glide.with(getApplicationContext()).load(customer.getImage()).into(image);
                        }
                    }
                    loading.setVisibility(View.GONE);
                    main.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener != null)
                    reference.child(offer.getUserId()).removeEventListener(listener);
                loading.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
                helpers.showError(OfferVenderDetail.this, "ERROR!", "Something went wrong please try again later.");
            }
        };
        reference.child(offer.getUserId()).addValueEventListener(listener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
