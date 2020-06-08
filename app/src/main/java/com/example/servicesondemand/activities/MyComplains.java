package com.example.servicesondemand.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyComplains extends AppCompatActivity {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Complains");
    private ValueEventListener listener;
    private Helpers helpers;
    private Session session;
    private User user;
    private RecyclerView complainsList;
    private LinearLayout loading;
    private TextView noRecordFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complains);

        helpers = new Helpers();
        session = new Session(getApplicationContext());
        user = session.getUser();
        complainsList = findViewById(R.id.complainsList);
        loading = findViewById(R.id.loading);
        noRecordFound = findViewById(R.id.noRecordFound);
        loadComplains();
    }

    private void loadComplains() {
        if (!helpers.isConnected(getApplicationContext())) {
            helpers.showError(MyComplains.this, "ERROR!", "No internet connection found.\nPlease connect to a network and try again.");
            loading.setVisibility(View.GONE);
            complainsList.setVisibility(View.GONE);
            noRecordFound.setVisibility(View.VISIBLE);
            return;
        }

        loading.setVisibility(View.VISIBLE);
        complainsList.setVisibility(View.GONE);
        noRecordFound.setVisibility(View.GONE);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listener != null)
                    reference.orderByChild("userId").equalTo(user.getId()).removeEventListener(listener);

                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener != null)
                    reference.orderByChild("userId").equalTo(user.getId()).removeEventListener(listener);
                loading.setVisibility(View.GONE);
                complainsList.setVisibility(View.GONE);
                noRecordFound.setVisibility(View.VISIBLE);
            }
        };

        reference.orderByChild("userId").equalTo(user.getId()).addValueEventListener(listener);
    }
}