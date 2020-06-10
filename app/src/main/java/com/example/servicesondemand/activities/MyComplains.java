package com.example.servicesondemand.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicesondemand.R;
import com.example.servicesondemand.adapter.ComplainAdapter;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.Complain;
import com.example.servicesondemand.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyComplains extends AppCompatActivity {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Complains");
    private ValueEventListener listener;
    private Helpers helpers;
    private User user;
    private RecyclerView complainsList;
    private LinearLayout loading;
    private TextView noRecordFound;
    private ComplainAdapter adapter;
    private List<Complain> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complains);

        helpers = new Helpers();
        Session session = new Session(getApplicationContext());
        user = session.getUser();
        complainsList = findViewById(R.id.complainsList);
        loading = findViewById(R.id.loading);
        noRecordFound = findViewById(R.id.noRecordFound);

        complainsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new ComplainAdapter(getApplicationContext());
        complainsList.setAdapter(adapter);
        data = new ArrayList<>();
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
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Complain c = d.getValue(Complain.class);
                    if (c != null) {
                        data.add(c);
                    }
                }
                loading.setVisibility(View.GONE);
                adapter.setData(data);
                if (data.size() > 0) {
                    noRecordFound.setVisibility(View.GONE);
                    complainsList.setVisibility(View.VISIBLE);
                } else {
                    noRecordFound.setVisibility(View.VISIBLE);
                    complainsList.setVisibility(View.GONE);
                }
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