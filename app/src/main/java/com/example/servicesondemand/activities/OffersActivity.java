package com.example.servicesondemand.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.servicesondemand.R;
import com.example.servicesondemand.adapter.OfferAdapter;
import com.example.servicesondemand.adapter.PostAdapter;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.Offer;
import com.example.servicesondemand.model.Post;
import com.example.servicesondemand.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OffersActivity extends AppCompatActivity {
    private RecyclerView myOffersList;
    private LinearLayout loading;
    private TextView noRecordFound;
    private Helpers helpers;
    private User user;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Offers");
    private ValueEventListener eventListener;
    private List<Offer> offers;
    private OfferAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        myOffersList = findViewById(R.id.myoffersList);
        loading = findViewById(R.id.loading);
        noRecordFound = findViewById(R.id.noRecordFound);

        Session session = new Session(getApplicationContext());
        user = session.getUser();
        helpers = new Helpers();
        offers = new ArrayList<>();

        myOffersList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new OfferAdapter(getApplicationContext(), user.getType(), new Post());
        myOffersList.setAdapter(adapter);
        loadMyOffers();
    }

    private void loadMyOffers() {
        if (!helpers.isConnected(getApplicationContext())) {
            helpers.showError(OffersActivity.this, "ERROR!", "No internet connection found.\nPlease connect to a network and try again.");
            return;
        }
        myOffersList.setVisibility(View.GONE);
        noRecordFound.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (eventListener != null)
                    reference.orderByChild("workerId").equalTo(user.getId()).removeEventListener(eventListener);
                Log.e("MyOffers", "Data Snap Shot: " + dataSnapshot.toString());
                offers.clear(); // Remove data, to avoid duplication
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Offer p = d.getValue(Offer.class);
                    offers.add(p);
                }
                Collections.reverse(offers); // Reverse the data list, to display the latest booking on top.
                Log.e("MyOffers", "Data List Size: " + offers.size());
                if (offers.size() > 0) {
                    Log.e("MyOffers", "If, list visible");
                    myOffersList.setVisibility(View.VISIBLE);
                    noRecordFound.setVisibility(View.GONE);
                } else {
                    Log.e("MyOffers", "Else, list invisible");
                    noRecordFound.setVisibility(View.VISIBLE);
                    myOffersList.setVisibility(View.GONE);
                }
                loading.setVisibility(View.GONE);
                adapter.setData(offers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (eventListener != null)
                    reference.orderByChild("workerId").equalTo(user.getId()).removeEventListener(eventListener);

                loading.setVisibility(View.GONE);
                noRecordFound.setVisibility(View.VISIBLE);
                myOffersList.setVisibility(View.GONE);
            }
        };

        reference.orderByChild("workerId").equalTo(user.getId()).addValueEventListener(eventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null)
            reference.removeEventListener(eventListener);
    }

    @Override
    public void onBackPressed() {
        finish();
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
}
