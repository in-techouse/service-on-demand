package com.example.servicesondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.servicesondemand.R;
import com.example.servicesondemand.adapter.PostAdapter;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.Post;
import com.example.servicesondemand.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VendorDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private User user;
    private Session session;
    private PostAdapter adapter;
    private RecyclerView myPostsList;
    private LinearLayout loading;
    private TextView noRecordFound;
    private Helpers helpers;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Jobs");
    private ValueEventListener eventListener;
    private List<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        session = new Session(VendorDashboard.this);
        user = session.getUser();

        View header = navigationView.getHeaderView(0);
        TextView name = header.findViewById(R.id.name);
        TextView phone = header.findViewById(R.id.phone);
        TextView email = header.findViewById(R.id.email);
        TextView category = header.findViewById(R.id.category);
        TextView perHourCharge = header.findViewById(R.id.perHourCharge);
        CircleImageView image = header.findViewById(R.id.image);

        if (user.getImage() != null && user.getImage().length() > 0) {
            Glide.with(getApplicationContext()).load(user.getImage()).into(image);
        }

        name.setText(user.getFirstName() + " " + user.getLastName());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
        category.setText(user.getCategory());
        perHourCharge.setText(user.getPerHourCharge() + " RS / Hr");

        myPostsList = findViewById(R.id.myPostsList);
        loading = findViewById(R.id.loading);
        noRecordFound = findViewById(R.id.noRecordFound);

        myPostsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new PostAdapter(getApplicationContext());
        myPostsList.setAdapter(adapter);

        helpers = new Helpers();
        posts = new ArrayList<>();
        loadPosts();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.e("Dashboard", "" + id);
        switch (id) {
            case R.id.nav_posts: {
                break;
            }
            case R.id.nav_orders: {
                Intent intent = new Intent(VendorDashboard.this, MyOrders.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_offers: {
                Intent intent = new Intent(VendorDashboard.this, OffersActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_notifications: {
                Intent intent = new Intent(VendorDashboard.this, MyNotifications.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_edit_profile: {
                Intent intent = new Intent(VendorDashboard.this, EditProfile.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_logout: {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null)
                    auth.signOut();
                session.destroySession();
                Intent intent = new Intent(VendorDashboard.this, GetStarted.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void loadPosts() {
        if (!helpers.isConnected(getApplicationContext())) {
            helpers.showError(VendorDashboard.this, "ERROR!", "No internet connection found.\nPlease connect to a network and try again.");
            return;
        }

        myPostsList.setVisibility(View.GONE);
        noRecordFound.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("MyPosts", "Data Snap Shot: " + dataSnapshot.toString());
                posts.clear(); // Remove data, to avoid duplication
                boolean flag = false;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Post p = d.getValue(Post.class);
                    if (p != null && p.getStatus().equals("Posted")) {
                        posts.add(p);
                        if (p.getOffers() == 0)
                            flag = true;
                    }
                }
                Collections.reverse(posts); // Reverse the data list, to display the latest booking on top.
                Log.e("MyPosts", "Data List Size: " + posts.size());
                if (posts.size() > 0) {
                    Log.e("MyPosts", "If, list visible");
                    myPostsList.setVisibility(View.VISIBLE);
                    noRecordFound.setVisibility(View.GONE);
                } else {
                    Log.e("MyPosts", "Else, list invisible");
                    noRecordFound.setVisibility(View.VISIBLE);
                    myPostsList.setVisibility(View.GONE);
                }
                loading.setVisibility(View.GONE);
                adapter.setData(posts);
                if (flag) {
                    helpers.showNotification(VendorDashboard.this, "NEW JOB FOUND", "We have got a new job for you");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                noRecordFound.setVisibility(View.VISIBLE);
                myPostsList.setVisibility(View.GONE);
            }
        };

        reference.orderByChild("category").equalTo(user.getCategory()).addValueEventListener(eventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null)
            reference.orderByChild("category").equalTo(user.getCategory()).removeEventListener(eventListener);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
