package com.example.servicesondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.Post;
import com.example.servicesondemand.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderDetail extends AppCompatActivity {
    private static final String TAG = "OrderDetail";
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listener;
    private Helpers helpers;
    private User user, otherUser;
    private Post post;
    private LinearLayout loading, main;
    protected CircleImageView image;
    private TextView detailLabel, phoneNumber, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent it = getIntent();
        if (it == null) {
            Log.e(TAG, "Intent is null");
            finish();
            return;
        }

        Bundle bundle = it.getExtras();
        if (bundle == null) {
            Log.e(TAG, "Bundle is null");
            finish();
            return;
        }

        post = (Post) bundle.getSerializable("post");

        if (post == null) {
            Log.e(TAG, "Post is null");
            finish();
            return;
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setHomeButtonEnabled(true);

        TextView date = findViewById(R.id.date);
        TextView time = findViewById(R.id.time);
        TextView offers = findViewById(R.id.offers);
        TextView category = findViewById(R.id.category);
        TextView status = findViewById(R.id.status);
        TextView address = findViewById(R.id.address);
        TextView description = findViewById(R.id.description);

        date.setText(post.getDate());
        time.setText(post.getTime());
        offers.setText(post.getOffers() + "");
        category.setText(post.getCategory());
        status.setText(post.getStatus());
        address.setText(post.getAddress());
        description.setText(post.getDescription());

        helpers = new Helpers();
        Session session = new Session(getApplicationContext());
        user = session.getUser();

        SliderLayout slider = findViewById(R.id.slider);
        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(2000);

        for (String str : post.getImages()) {
            TextSliderView textSliderView = new TextSliderView(OrderDetail.this);
            textSliderView
                    .description("")
                    .image(str)
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            slider.addSlider(textSliderView);
        }

        detailLabel = findViewById(R.id.detailLabel);
        name = findViewById(R.id.name);
        image = findViewById(R.id.image);
        phoneNumber = findViewById(R.id.phoneNumber);
        email = findViewById(R.id.email);

        loading = findViewById(R.id.loading);
        main = findViewById(R.id.main);
        if (user.getType() == 1) {
            detailLabel.setText("CUSTOMER DETAILS");
        }
        loadOtherUserDetail();
    }

    private void loadOtherUserDetail() {
        loading.setVisibility(View.VISIBLE);
        main.setVisibility(View.GONE);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listener != null) {
                    if (user.getType() == 0)
                        reference.child("Users").child(post.getWorkerId()).removeEventListener(listener);
                    else
                        reference.child("Users").child(post.getUserId()).removeEventListener(listener);
                }
                if (dataSnapshot.exists()) {
                    otherUser = dataSnapshot.getValue(User.class);
                    if (otherUser != null) {
                        name.setText(otherUser.getFirstName() + " " + otherUser.getLastName());
                        phoneNumber.setText(otherUser.getPhone());
                        email.setText(otherUser.getEmail());
                        if (otherUser.getImage() != null && otherUser.getImage().length() > 0) {
                            Glide.with(getApplicationContext()).load(otherUser.getImage()).into(image);
                        }
                    }
                }
                loading.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener != null) {
                    if (user.getType() == 0)
                        reference.child("Users").child(post.getWorkerId()).removeEventListener(listener);
                    else
                        reference.child("Users").child(post.getUserId()).removeEventListener(listener);
                }
                helpers.showError(OrderDetail.this, "ERROR", "SOMETHING WENT WRONG PLEASE TRY LATER");

                loading.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
            }
        };
        if (user.getType() == 0)
            reference.child("Users").child(post.getWorkerId()).addValueEventListener(listener);
        else
            reference.child("Users").child(post.getUserId()).addValueEventListener(listener);

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
