package com.example.servicesondemand.activities;

import android.os.Bundle;
import android.util.Log;
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
import com.example.servicesondemand.model.Order;
import com.example.servicesondemand.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyOrders extends AppCompatActivity {

    private RecyclerView ordersList;
    private LinearLayout loading;
    private TextView noRecordFound;
    private Helpers helpers;
    private Session session;
    private User user;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Orders");
    private ValueEventListener eventListener;
    private List<Order> orders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        ordersList = findViewById(R.id.ordersList);
        loading = findViewById(R.id.loading);
        noRecordFound = findViewById(R.id.noRecordFound);

        session = new Session(getApplicationContext());

        user = session.getUser();
        helpers = new Helpers();
        orders = new ArrayList<>();
        loadOrders();
    }

    private void loadOrders() {
        if (!helpers.isConnected(getApplicationContext())) {
            helpers.showError(MyOrders.this, "ERROR!", "No internet connection found.\nPlease connect to a network and try again.");
            return;
        }

        ordersList.setVisibility(View.GONE);
        noRecordFound.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);


        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("MyOrders", "Data Snap Shot: " + dataSnapshot.toString());
                orders.clear(); // Remove data, to avoid duplication
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Order c = d.getValue(Order.class);
                    if (c != null) {
                        orders.add(c);
                    }
                }
                Collections.reverse(orders); // Reverse the data list, to display the latest booking on top.
                Log.e("MyOrders", "Data List Size: " + orders.size());
                if (orders.size() > 0) {
                    Log.e("MyOrders", "If, list visible");
                    ordersList.setVisibility(View.VISIBLE);
                    noRecordFound.setVisibility(View.GONE);
                } else {
                    Log.e("MyOrders", "Else, list invisible");
                    noRecordFound.setVisibility(View.VISIBLE);
                    ordersList.setVisibility(View.GONE);
                }
                loading.setVisibility(View.GONE);
//                adapter.setData(orders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                noRecordFound.setVisibility(View.VISIBLE);
                ordersList.setVisibility(View.GONE);
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
