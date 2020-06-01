package com.example.servicesondemand.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.servicesondemand.R;
import com.example.servicesondemand.model.Offers;
import com.example.servicesondemand.model.Post;

public class OfferDetail extends AppCompatActivity {
    private static final String TAG = "OfferDetail";
    private Offers offers;

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

        offers = (Offers) bundle.getSerializable("offers");

        if (offers == null) {
            Log.e(TAG, "Offer is null");
            finish();
            return;
        }

    }
}
