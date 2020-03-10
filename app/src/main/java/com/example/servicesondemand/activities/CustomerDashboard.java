package com.example.servicesondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private User user;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(CustomerDashboard.this, CreatePost.class);
                startActivity(it);
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        session = new Session(CustomerDashboard.this);
        user  = session.getUser();

        View header = navigationView.getHeaderView(0);
        TextView name = header.findViewById(R.id.name);
        TextView phone = header.findViewById(R.id.phone);
        TextView email = header.findViewById(R.id.email);
        CircleImageView image = header.findViewById(R.id.image);

        if(user.getImage() != null && user.getImage().length() > 0){
            Glide.with(getApplicationContext()).load(user.getImage()).into(image);
        }

        name.setText(user.getFirstName() + " " + user.getLastName());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.e("Dashboard", "" + id);
        switch (id) {
            case R.id.nav_my_post: {
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
            case R.id.nav_edit_profile: {
                Intent intent = new Intent(CustomerDashboard.this, EditProfile.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_logout: {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if(auth.getCurrentUser() != null)
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
}
