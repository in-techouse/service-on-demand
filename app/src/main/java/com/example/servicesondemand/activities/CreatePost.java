package com.example.servicesondemand.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.model.Category;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class CreatePost extends AppCompatActivity {
    private static final String TAG = "MakePost";
    private Category category;
    private Button post;
    private TextView mDisplayedDate, mDisplayedTime, categoryTv, address;
    private EditText post_description;
    private RelativeLayout selectDate, selectTime, selectAddress;
    private DatePickerDialog.OnDateSetListener mDatesetlistener;
    private TimePickerDialog.OnTimeSetListener mTimesetlistener;
    private Helpers helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent it = getIntent();

        if (it == null) {
            Log.e(TAG, "Intent is null");
            return;
        }
        Bundle bundle = it.getExtras();
        if (bundle == null) {
            Log.e(TAG, "Bundle is null");
            return;
        }
        category = (Category) bundle.getSerializable("category");
        if (category == null) {
            Log.e(TAG, "Category is null");
            return;
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helpers = new Helpers();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        post = findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = helpers.isConnected(CreatePost.this);
                if (!isConnected) {
                    helpers.showError(CreatePost.this, "ERROR", "NO INTERNET CONNECTION FOUND PLEASE CHECK INTERNET");
                    return;
                }

                String strDate = mDisplayedDate.getText().toString();
                String strTime = mDisplayedTime.getText().toString();
                String strAddress = address.getText().toString();
                String strDescription = post_description.getText().toString();
                boolean flag = true;
                String error = "";

                if (strDate.equals("Select Date")) {
                    error = error + "Select your job date first.\n";
                    flag = false;
                }

                if (strTime.equals("Select Time")) {
                    error = error + "Select your job time first.\n";
                    flag = false;
                }

                if (strAddress.equals("Select Address")) {
                    error = error + "Select your job address first.\n";
                    flag = false;
                }

                if (strDescription.length() < 10) {
                    post_description.setError("Enter a valid description");
                    flag = false;
                }

                if (error.length() > 0) {
                    helpers.showError(CreatePost.this, "ERROR!", error);
                }

                if (flag) {
                    // Everything is good
                } else {
                    // Inputs are not valid
                }

            }
        });

        AppBarLayout app_bar = findViewById(R.id.app_bar);
        app_bar.setExpanded(true);

        post_description = findViewById(R.id.post_description);
        // Category
        categoryTv = findViewById(R.id.category);
        categoryTv.setFocusable(true);
        categoryTv.setFocusableInTouchMode(true);
        categoryTv.setText("Posting for " + category.getName());
        app_bar.setExpanded(true);
        // Date
        mDisplayedDate = findViewById(R.id.date);
        selectDate = findViewById(R.id.selectDate);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(CreatePost.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        mDatesetlistener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDatesetlistener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.e(TAG, "onDateSet: mm/dd/yy: " + month + "/" + day + "/" + year);
                String date = month + "/" + day + "/" + year;
                mDisplayedDate.setText(date);
            }
        };

        // Time
        mDisplayedTime = findViewById(R.id.time);
        selectTime = findViewById(R.id.selectTime);
        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(CreatePost.this, mTimesetlistener, hour, minute, false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mTimesetlistener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.e(TAG, "onTimeSet: hh:mm: " + hour + ":" + minute);
                String time = hour + ":" + minute;
                mDisplayedTime.setText(time);
            }
        };

        // Address
        selectAddress = findViewById(R.id.selectAddress);
        address = findViewById(R.id.address);
        selectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(CreatePost.this, SelectAddress.class);
                startActivityForResult(it, 10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "Request Code: " + requestCode);
        Log.e(TAG, "Result Code: " + resultCode);
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
