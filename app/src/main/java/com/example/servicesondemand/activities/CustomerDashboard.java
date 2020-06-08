package com.example.servicesondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.servicesondemand.R;
import com.example.servicesondemand.adapter.CategoryAdapter;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.Category;
import com.example.servicesondemand.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private User user;
    private Session session;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Categories");
    private ValueEventListener eventListener;
    private List<Category> categories;
    private GridView gridView;
    private LinearLayout loading;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        session = new Session(CustomerDashboard.this);
        user = session.getUser();

        View header = navigationView.getHeaderView(0);
        TextView name = header.findViewById(R.id.name);
        TextView phone = header.findViewById(R.id.phone);
        TextView email = header.findViewById(R.id.email);
        CircleImageView image = header.findViewById(R.id.image);

        if (user.getImage() != null && user.getImage().length() > 0) {
            Glide.with(getApplicationContext()).load(user.getImage()).into(image);
        }

        name.setText(user.getFirstName() + " " + user.getLastName());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());

        gridView = findViewById(R.id.gridView);
        loading = findViewById(R.id.loading);

        categories = new ArrayList<>();
        adapter = new CategoryAdapter(getApplicationContext());
        gridView.setAdapter(adapter);
        loadCategories();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("CustomerDashboard", "Item Clicked at position: " + position);
                Intent it = new Intent(CustomerDashboard.this, CreatePost.class);
                Bundle bundle = new Bundle();
                Category category = categories.get(position);
                bundle.putSerializable("category", category);
                it.putExtras(bundle);
                startActivity(it);
            }
        });
    }

    private void loadCategories() {
        loading.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.GONE);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.orderByChild("name").removeEventListener(eventListener);
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Category cat = data.getValue(Category.class);
                    if (cat != null) {
                        categories.add(cat);
                    }
                }
                adapter.setCategories(categories);
                loading.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                reference.orderByChild("name").removeEventListener(eventListener);
                loading.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
            }
        };
        reference.orderByChild("name").addValueEventListener(eventListener);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_my_post: {
                Intent intent = new Intent(CustomerDashboard.this, MyPosts.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_orders: {
                Intent intent = new Intent(CustomerDashboard.this, MyOrders.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_notifications: {
                Intent intent = new Intent(CustomerDashboard.this, MyNotifications.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_complains: {
                Intent intent = new Intent(CustomerDashboard.this, MyComplains.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_edit_profile: {
                Intent intent = new Intent(CustomerDashboard.this, EditProfile.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_logout: {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null)
                    auth.signOut();
                session.destroySession();
                Intent intent = new Intent(CustomerDashboard.this, GetStarted.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null) {
            reference.removeEventListener(eventListener);
        }
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
