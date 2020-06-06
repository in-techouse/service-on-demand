package com.example.servicesondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.servicesondemand.R;
import com.example.servicesondemand.model.Offer;

public class OfferDetail extends AppCompatActivity {
    private static final String TAG = "OfferDetail";
    private Offer offer;
    private TextView budget, time, description;

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

        budget.setText(offer.getBudgetOffered() + " RS");
        time.setText(offer.getTimeRequired() + " Minutes");
        description.setText(offer.getDescription());

    }


}
