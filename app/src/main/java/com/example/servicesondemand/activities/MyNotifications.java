package com.example.servicesondemand.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.Notification;
import com.example.servicesondemand.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyNotifications extends AppCompatActivity {
    private RecyclerView notificationsList;
    private LinearLayout loading;
    private TextView noRecordFound;
    private Helpers helpers;
    private Session session;
    private User user;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications");
    private ValueEventListener eventListener;
    private List<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notifications);

        notificationsList = findViewById(R.id.notificationsList);
        loading = findViewById(R.id.loading);
        noRecordFound = findViewById(R.id.noRecordFound);

        session = new Session(getApplicationContext());

        user = session.getUser();
        helpers = new Helpers();
        notifications = new ArrayList<>();
        loadNotifications();
    }

    private void loadNotifications() {
        if (!helpers.isConnected(getApplicationContext())) {
            helpers.showError(MyNotifications.this, "ERROR!", "No internet connection found.\nPlease connect to a network and try again.");
            return;
        }

        notificationsList.setVisibility(View.GONE);
        noRecordFound.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);


        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
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
