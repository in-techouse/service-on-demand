package com.example.servicesondemand.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicesondemand.R;
import com.example.servicesondemand.adapter.NotificationAdapter;
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
import java.util.Collections;
import java.util.List;

public class MyNotifications extends AppCompatActivity {
    private RecyclerView notificationsList;
    private LinearLayout loading;
    private TextView noRecordFound;
    private Helpers helpers;
    private User user;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications");
    private ValueEventListener eventListener;
    private List<Notification> notifications;
    private NotificationAdapter adapter;
    private String orderBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notifications);

        notificationsList = findViewById(R.id.notificationsList);
        loading = findViewById(R.id.loading);
        noRecordFound = findViewById(R.id.noRecordFound);

        Session session = new Session(getApplicationContext());
        user = session.getUser();
        helpers = new Helpers();
        notifications = new ArrayList<>();

        notificationsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new NotificationAdapter(user.getType());
        notificationsList.setAdapter(adapter);

        if (user.getType() == 0) {
            orderBy = "userId";
        } else {
            orderBy = "workerId";
        }
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
                Log.e("MyNotifications", "Data Snap Shot: " + dataSnapshot.toString());
                notifications.clear(); // Remove data, to avoid duplication
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Notification c = d.getValue(Notification.class);
                    if (c != null) {
                        notifications.add(c);
                    }
                }
                Collections.reverse(notifications); // Reverse the data list, to display the latest booking on top.
                Log.e("MyNotifications", "Data List Size: " + notifications.size());
                if (notifications.size() > 0) {
                    Log.e("MyNotifications", "If, list visible");
                    notificationsList.setVisibility(View.VISIBLE);
                    noRecordFound.setVisibility(View.GONE);
                } else {
                    Log.e("MyNotifications", "Else, list invisible");
                    noRecordFound.setVisibility(View.VISIBLE);
                    notificationsList.setVisibility(View.GONE);
                }
                loading.setVisibility(View.GONE);
                adapter.setData(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                noRecordFound.setVisibility(View.VISIBLE);
                notificationsList.setVisibility(View.GONE);
            }
        };

        reference.orderByChild(orderBy).equalTo(user.getId()).addValueEventListener(eventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null)
            reference.orderByChild(orderBy).equalTo(user.getId()).removeEventListener(eventListener);
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
