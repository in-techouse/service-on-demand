package com.example.servicesondemand.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout perHourChargeUpper, categoryUpper;
    private EditText firstName, lastName, email, mobile, category, perHourCharge;
    private Button update;
    private Helpers helpers;
    private Session session;
    private User user;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        update = findViewById(R.id.update);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        category = findViewById(R.id.category);
        perHourCharge = findViewById(R.id.perHourCharge);
        image = findViewById(R.id.image);
        perHourChargeUpper = findViewById(R.id.perHourChargeUpper);
        categoryUpper = findViewById(R.id.categoryUpper);

        fab.setOnClickListener(this);
        update.setOnClickListener(this);

        session = new Session(getApplicationContext());
        user = session.getUser();
        helpers = new Helpers();

        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
        mobile.setText(user.getPhone());
        category.setText(user.getCategory());
        perHourCharge.setText(user.getPerHourCharge() + "");
        if (user.getImage() != null && user.getImage().length() > 1) {
            Glide.with(getApplicationContext()).load(user.getImage()).into(image);
        } else {
            image.setImageDrawable(getResources().getDrawable(R.drawable.user));
        }

        if (user.getType() == 0) {
            perHourChargeUpper.setVisibility(View.GONE);
            categoryUpper.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.update: {
                if (helpers.isConnected(getApplicationContext())) {

                } else {
                    helpers.showError(EditProfile.this, "ERROR!", "No internet connection found.\nConnect to a network and try again.");
                }
                break;
            }
            case R.id.fab: {
                break;
            }
        }
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
