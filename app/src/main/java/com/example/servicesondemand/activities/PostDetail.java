package com.example.servicesondemand.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.servicesondemand.R;
import com.example.servicesondemand.adapter.OfferAdapter;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.Offer;
import com.example.servicesondemand.model.Post;
import com.example.servicesondemand.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostDetail extends AppCompatActivity {
    private static final String TAG = "PostDetail";
    private EditText Response, PerHourCharge, JobTime;
    private String strResponse, strPerHourCharge, strJobTime;
    private Button SendResponse;
    private Session session;
    private User user;
    private Helpers helpers;
    private Post post;
    private SliderLayout slider;
    private TextView date, time, offers, category, status, address, description;
    private ProgressDialog loadingBar;
    private LinearLayout sendOffer;
    private RecyclerView offersList;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Offers");
    private ValueEventListener eventListener;
    private OfferAdapter adapter;
    private List<Offer> jobOffers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
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

        jobOffers = new ArrayList<>();
        SendResponse = findViewById(R.id.sendresponse);
        Response = findViewById(R.id.response);
        PerHourCharge = findViewById(R.id.perHourCharge);
        JobTime = findViewById(R.id.jobtime);
        sendOffer = findViewById(R.id.sendOffer);
        offersList = findViewById(R.id.offersList);

        helpers = new Helpers();

        SendResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("PostDetail", "On Click");
                boolean flag = helpers.isConnected(PostDetail.this);
                if (!flag) {
                    helpers.showError(PostDetail.this, "ERROR", "NO INTERNET CONNECTION FOUND PLEASE CHECK INTERNET");
                    return;
                }
                boolean isFlag = isValid();
                Log.e("PostDetail", "is Valid: " + isFlag);

                if (isFlag) {
                    Log.e("PostDetail", "Saving");
                    // Everything is valid
                    loadingBar.setTitle("SAVING");
                    loadingBar.setMessage("Please wait, while we are sending your offer to the job owner...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.setCancelable(false);
                    loadingBar.show();
                    Offer offer = new Offer();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Offers");
                    String id = reference.push().getKey();
                    offer.setId(id);
                    offer.setWorkerId(user.getId());
                    offer.setUserId(post.getUserId());
                    offer.setDescription(strResponse);
                    offer.setBudgetOffered(Integer.parseInt(strPerHourCharge));
                    offer.setJobId(post.getId());
                    offer.setStatus("Sent");
                    offer.setTimeRequired(Integer.parseInt(strJobTime));
                    reference.child(offer.getId()).setValue(offer)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loadingBar.dismiss();
                                    helpers.showSuccess(PostDetail.this, "Offer POSTED!", "Your Offer has been posted successfully.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingBar.dismiss();
                                    helpers.showError(PostDetail.this, "ERROR!", "Offer not posted.\nSomething went wrong.\nPlease try again later,");
                                }
                            });

                }
            }
        });

        session = new Session(getApplicationContext());
        user = session.getUser();

        slider = findViewById(R.id.slider);
        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(2000);

        for (String str : post.getImages()) {
            TextSliderView textSliderView = new TextSliderView(PostDetail.this);
            textSliderView
                    .description("")
                    .image(str)
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            slider.addSlider(textSliderView);
        }

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        offers = findViewById(R.id.offers);
        category = findViewById(R.id.category);
        status = findViewById(R.id.status);
        address = findViewById(R.id.address);
        description = findViewById(R.id.description);

        date.setText(post.getDate());
        time.setText(post.getTime());
        offers.setText(post.getOffers() + "");
        category.setText(post.getCategory());
        status.setText(post.getStatus());
        address.setText(post.getAddress());
        description.setText(post.getDescription());

        loadingBar = new ProgressDialog(this);

        if (user.getType() == 0) { //Check for checking the status of job k ye task kisi worker
            offersList.setVisibility(View.VISIBLE);
            sendOffer.setVisibility(View.GONE);
            LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext());
            linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
            linearLayout.setAutoMeasureEnabled(true);
            offersList.setLayoutManager(linearLayout);
            adapter = new OfferAdapter(getApplicationContext(), user.getType(), post);
            offersList.setAdapter(adapter);
            loadMyOffers();
        } else {
            offersList.setVisibility(View.GONE);
            if (post.getStatus().equalsIgnoreCase("Posted") && post.getWorkerId().length() < 1) {
                sendOffer.setVisibility(View.VISIBLE);
            } else {
                sendOffer.setVisibility(View.GONE);
            }
        }
    }

    private void loadMyOffers() {
        if (!helpers.isConnected(getApplicationContext())) {
            helpers.showError(PostDetail.this, "ERROR!", "No internet connection found.\nPlease connect to a network and try again.");
            return;
        }

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (eventListener != null)
                    reference.orderByChild("jobId").equalTo(post.getId()).removeEventListener(eventListener);
                Log.e("PostDetail", "Data Snap Shot: " + dataSnapshot.toString());
                jobOffers.clear(); // Remove data, to avoid duplication
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Offer p = d.getValue(Offer.class);
                    jobOffers.add(p);
                }
                Collections.reverse(jobOffers); // Reverse the data list, to display the latest booking on top.
                Log.e("PostDetail", "Data List Size: " + jobOffers.size());
                adapter.setData(jobOffers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (eventListener != null)
                    reference.orderByChild("jobId").equalTo(post.getId()).removeEventListener(eventListener);

            }
        };

        reference.orderByChild("jobId").equalTo(post.getId()).addValueEventListener(eventListener);
    }

    private boolean isValid() {
        boolean flag = true;
        strResponse = Response.getText().toString();
        strPerHourCharge = PerHourCharge.getText().toString();
        strJobTime = JobTime.getText().toString();

        if (strResponse.length() < 5) {
            Response.setError("Enter your offer.");
            flag = false;
        } else {
            Response.setError(null);
        }

        if (strPerHourCharge.length() < 1) {
            PerHourCharge.setError("Enter per hour charge");
            flag = false;
        } else {
            PerHourCharge.setError(null);
        }
        if (strJobTime.length() < 2) {
            JobTime.setError("Enter time");
            flag = false;
        } else {
            JobTime.setError(null);
        }
        return flag;
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
