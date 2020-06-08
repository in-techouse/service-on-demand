package com.example.servicesondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfferDetail extends AppCompatActivity {
    private static final String TAG = "OfferDetail";
    private Offer offer;
    private TextView budget, time, description, phoneNumber, name, email;
    private Button acceptOffer;
    private Helpers helpers;
    private ScrollView main;
    private LinearLayout loading;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listener;
    private User vendor;
    protected CircleImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);

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
        time = findViewById(R.id.time);
        budget = findViewById(R.id.budget);
        description = findViewById(R.id.description);
        loading = findViewById(R.id.loading);
        main = findViewById(R.id.main);
        helpers = new Helpers();
        name = findViewById(R.id.name);
        image = findViewById(R.id.image);
        phoneNumber = findViewById(R.id.phoneNumber);
        email = findViewById(R.id.email);
        acceptOffer = findViewById(R.id.acceptOffer);

        acceptOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = helpers.isConnected(OfferDetail.this);
                if (!isConnected) {
                    helpers.showError(OfferDetail.this, "ERROR", "NO INTERNET CONNECTION FOUND PLEASE CHECK INTERNET");
                    return;
                }
                loading.setVisibility(View.VISIBLE);
                main.setVisibility(View.GONE);
                offer.setStatus("Accepted");
                reference.child("Offers").child(offer.getId()).setValue(offer).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loading.setVisibility(View.GONE);
                        main.setVisibility(View.VISIBLE);
                        helpers.showSuccess(OfferDetail.this, "OFFER_ACCEPTED", "OFFER ACCEPTED SUCCESFULLY");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.setVisibility(View.VISIBLE);
                        main.setVisibility(View.GONE);
                        helpers.showError(OfferDetail.this, "ERROR", "SOMETHING WENT WRONG PLEASE CHECK AGAIN LATER");
                    }
                });

            }
        });



        budget.setText(offer.getBudgetOffered() + " RS");
        time.setText(offer.getTimeRequired() + " Minutes");
        description.setText(offer.getDescription());

        loadVendorDetail();
    }

    private void loadVendorDetail() {
        loading.setVisibility(View.VISIBLE);
        main.setVisibility(View.GONE);


        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    vendor = dataSnapshot.getValue(User.class);

                    if (vendor != null) {
                        name.setText(vendor.getFirstName() + " " + vendor.getLastName());
                        phoneNumber.setText(vendor.getPhone());
                        email.setText(vendor.getEmail());
                        if (vendor.getImage() != null && vendor.getImage().length() > 0) {
                            Glide.with(getApplicationContext()).load(vendor.getImage()).into(image);
                        }
                    }
                    loading.setVisibility(View.GONE);
                    main.setVisibility(View.VISIBLE);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
                helpers.showError(OfferDetail.this, "ERROR", "SOMETHING WENT WRONG PLEASE TRY LATER");
            }
        };

        reference.child("Users").child(offer.getWorkerId()).addValueEventListener(listener);
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
