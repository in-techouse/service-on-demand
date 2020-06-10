package com.example.servicesondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderDetail extends AppCompatActivity {
    private static final String TAG = "OrderDetail";
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listener, jobListener;
    private Helpers helpers;
    private User user, otherUser;
    private Post post;
    private LinearLayout loading, main;
    protected CircleImageView image;
    private TextView phoneNumber, name, email;
    private AppCompatButton cancelJob, markCompleteJob;

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
        TextView budget = findViewById(R.id.budget);

        date.setText(post.getDate());
        time.setText(post.getTime());
        offers.setText(post.getOffers() + "");
        category.setText(post.getCategory());
        status.setText(post.getStatus());
        address.setText(post.getAddress());
        description.setText(post.getDescription());
        budget.setText(post.getBudget() + " Rs.");

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

        TextView detailLabel = findViewById(R.id.detailLabel);
        name = findViewById(R.id.name);
        image = findViewById(R.id.image);
        phoneNumber = findViewById(R.id.phoneNumber);
        email = findViewById(R.id.email);
        cancelJob = findViewById(R.id.cancelJob);
        markCompleteJob = findViewById(R.id.markCompleteJob);

        loading = findViewById(R.id.loading);
        main = findViewById(R.id.main);
        if (user.getType() == 1) {
            detailLabel.setText("CUSTOMER DETAILS");
        }
        if (post.getStatus().equalsIgnoreCase("Accepted")) {
            if (user.getType() == 0) {
                markCompleteJob.setVisibility(View.GONE);
            }
            activateJobListener();
        } else {
            markCompleteJob.setVisibility(View.GONE);
            cancelJob.setVisibility(View.GONE);
        }

        loadOtherUserDetail();

        cancelJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelConfirmation();
            }
        });

        markCompleteJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void cancelConfirmation() {
        MaterialDialog mDialog = new MaterialDialog.Builder(OrderDetail.this)
                .setTitle("ARE YOU SURE?")
                .setMessage("Are you sure to cancel the job with " + otherUser.getFirstName() + " " + otherUser.getLastName())
                .setCancelable(false)
                .setPositiveButton("YES", R.drawable.ok, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        cancelJob();
                    }
                })
                .setNegativeButton("NO!", R.drawable.cancel, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show Dialog
        mDialog.show();
    }

    private void cancelJob() {
        loading.setVisibility(View.VISIBLE);
        main.setVisibility(View.GONE);
        post.setStatus("Cancelled");
        reference.child("Jobs").child(post.getId()).setValue(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String userId = "";
                        String userText = "";
                        String workerId = "";
                        String workerText = "";
                        if (user.getType() == 0) {
                            userId = user.getId();
                            userText = "You cancelled your job with " + otherUser.getFirstName() + " " + otherUser.getLastName();
                            workerId = otherUser.getId();
                            workerText = "The job owner " + user.getFirstName() + " " + user.getLastName() + " cancelled the job.";
                        } else {
                            userId = otherUser.getId();
                            userText = "The vendor " + user.getFirstName() + " " + user.getLastName() + " cancelled the job.";
                            workerId = user.getId();
                            workerText = "You cancelled your job with " + otherUser.getFirstName() + " " + otherUser.getLastName();
                        }
                        helpers.sendNotification(userId, userText, workerId, workerText, post.getId());
                        loading.setVisibility(View.GONE);
                        main.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        helpers.showError(OrderDetail.this, "ERROR", "SOMETHING WENT WRONG PLEASE TRY LATER");
                        loading.setVisibility(View.GONE);
                        main.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void activateJobListener() {
        jobListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Post p = dataSnapshot.getValue(Post.class);
                    if (p != null) {
                        post = p;
                        switch (post.getStatus()) {
                            case "Cancelled": {
                                helpers.showSuccess(OrderDetail.this, "JOB CANCELLED", "The job has been cancelled");
                                break;
                            }
                            case "Completed": {
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reference.child("Jobs").child(post.getId()).addValueEventListener(jobListener);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (user.getType() == 0) {
            getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("OrderDetail", "Option Click Capture");
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.action_complain: {
                Intent it = new Intent(OrderDetail.this, SubmitComplain.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", post);
                bundle.putSerializable("vendor", otherUser);
                it.putExtras(bundle);
                startActivity(it);
                break;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jobListener != null)
            reference.child("Jobs").child(post.getId()).removeEventListener(jobListener);
    }
}
