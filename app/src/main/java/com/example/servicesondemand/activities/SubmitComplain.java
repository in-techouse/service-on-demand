package com.example.servicesondemand.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.Complain;
import com.example.servicesondemand.model.Post;
import com.example.servicesondemand.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SubmitComplain extends AppCompatActivity {
    private static final String TAG = "SubmitComplaint";
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Complains");
    private EditText description;
    private Helpers helpers;
    private User user, vendor;
    private Post post;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_complain);

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
        vendor = (User) bundle.getSerializable("vendor");

        if (post == null || vendor == null) {
            Log.e(TAG, "Post or Vendor is null");
            finish();
            return;
        }

        helpers = new Helpers();
        Session session = new Session(getApplicationContext());
        user = session.getUser();

        dialog = new ProgressDialog(this);

        description = findViewById(R.id.description);
        Button submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = helpers.isConnected(SubmitComplain.this);
                if (!isConnected) {
                    helpers.showError(SubmitComplain.this, "ERROR", "NO INTERNET CONNECTION FOUND PLEASE TRY AGAIN LATER");
                    return;
                }
                String strComplain = description.getText().toString();
                if (strComplain.length() < 10) {
                    description.setError("Enter a valid complain");
                    return;
                }
                dialog.setTitle("Submitting your complain");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                Complain complain = new Complain();
                String cId = reference.push().getKey();
                complain.setId(cId);
                complain.setUserId(user.getId());
                complain.setVendorId(vendor.getId());
                complain.setJobId(post.getId());
                complain.setComplain(strComplain);
                String strDateTime = new SimpleDateFormat("EEE, dd, MMM yyyy hh:mm a").format(new Date());
                complain.setDateTime(strDateTime);
                reference.child(complain.getId()).setValue(complain)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                helpers.showSuccess(SubmitComplain.this, "", "Your complain has been submitted successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                helpers.showError(SubmitComplain.this, "ERROR", "Something went wrong.\nPlease try again later.");
                            }
                        });
            }
        });
    }
}