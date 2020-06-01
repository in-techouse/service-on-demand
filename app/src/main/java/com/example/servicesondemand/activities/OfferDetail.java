package com.example.servicesondemand.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.servicesondemand.R;
import com.example.servicesondemand.model.Offer;
import com.example.servicesondemand.model.Offer;
import com.example.servicesondemand.model.Post;

public class OfferDetail extends AppCompatActivity {
    private static final String TAG = "OfferDetail";
    private Offer offer;

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

        offer = (Offer) bundle.getSerializable("offers");

        if (offer == null) {
            Log.e(TAG, "Offer is null");
            finish();
            return;
        }

    }
}
