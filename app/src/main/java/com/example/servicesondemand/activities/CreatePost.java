package com.example.servicesondemand.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.model.Category;
import com.example.servicesondemand.model.Post;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private Post postObj;
    private SliderLayout slider;
    private List<Uri> sliderImages = new ArrayList<>();

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
        postObj = new Post();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (askForPermission()) {
                    openGallery();
                }
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

        slider = findViewById(R.id.slider);

//        HashMap<String, String> url_maps = new HashMap<>();
//        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
//        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
//        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
//        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");
//
//        for (String name : url_maps.keySet()) {
//            TextSliderView textSliderView = new TextSliderView(this);
//            // initialize a SliderLayout
//            textSliderView
//                    .description(name)
//                    .image(url_maps.get(name))
//                    .setScaleType(BaseSliderView.ScaleType.Fit);
//
//            slider.addSlider(textSliderView);
//        }
        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(2000);
    }

    private boolean askForPermission() {
        if (ActivityCompat.checkSelfPermission(CreatePost.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(CreatePost.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreatePost.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "Request Code: " + requestCode);
        Log.e(TAG, "Result Code: " + resultCode);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Post p = (Post) bundle.getSerializable("result");
                    if (p != null) {
                        Log.e(TAG, "Location Received: " + p.getAddress());
                        postObj.setLatitude(p.getLatitude());
                        postObj.setLongitude(p.getLongitude());
                        postObj.setAddress(p.getAddress());
                        address.setText(postObj.getAddress());
                    }
                }
            }
        } else if (requestCode == 2) {
            Log.e(TAG, "Request Code Matched");
            if (resultCode == RESULT_OK) {
                Log.e(TAG, "Result Code Matched");
                if (data == null) {
                    Log.e(TAG, "Data null");
                    return;
                }
                Uri imageFile = data.getData();
                if (imageFile != null) {
                    Log.e(TAG, "Image is not null");
                    TextSliderView textSliderView = new TextSliderView(this);
                    Log.e(TAG, "Image path: " + imageFile.getPath());
                    File file = new File(imageFile.getPath());
//                    if (file.exists()) {
//                        Log.e(TAG, "File exists");
                        textSliderView
                                .description("")
                                .image(file)
                                .setScaleType(BaseSliderView.ScaleType.Fit);

                        slider.addSlider(textSliderView);
//                    }
//                    else{
//                        Log.e(TAG, "File doesn't exists");
//                    }
                }
            }
        }
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
