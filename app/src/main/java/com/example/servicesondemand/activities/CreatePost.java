package com.example.servicesondemand.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.ProgressBar;
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
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.Category;
import com.example.servicesondemand.model.Post;
import com.example.servicesondemand.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreatePost extends AppCompatActivity {
    private static final String TAG = "MakePost";
    private Button post;
    private TextView mDisplayedDate;
    private TextView mDisplayedTime;
    private TextView address;
    private EditText post_description;
    private DatePickerDialog.OnDateSetListener mDatesetlistener;
    private TimePickerDialog.OnTimeSetListener mTimesetlistener;
    private Helpers helpers;
    private Post postObj;
    private SliderLayout slider;
    private ProgressBar progress;
    private List<String> sliderImages = new ArrayList<>();
    private User user;

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
        Category category = (Category) bundle.getSerializable("category");
        if (category == null) {
            Log.e(TAG, "Category is null");
            return;
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helpers = new Helpers();
        postObj = new Post();
        Session session = new Session(getApplicationContext());
        user = session.getUser();
        postObj.setCategory(category.getName());
        postObj.setStatus("Posted");
        postObj.setWorkerId("");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (askForPermission()) {
                    openGallery();
                }
            }
        });

        progress = findViewById(R.id.progress);
        progress.setVisibility(View.GONE);


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
                postObj.setDate(strDate);
                postObj.setTime(strTime);
                postObj.setDescription(strDescription);
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

                if (sliderImages.size() < 1) {
                    error = error + "Select at least one image for the job you want to post.\n";
                    flag = false;
                }

                if (error.length() > 0) {
                    helpers.showError(CreatePost.this, "ERROR!", error);
                }

                if (flag) {
                    // Everything is good
                    saveJob();
                }
            }
        });

        AppBarLayout app_bar = findViewById(R.id.app_bar);
        app_bar.setExpanded(true);

        post_description = findViewById(R.id.post_description);
        // Category
        TextView categoryTv = findViewById(R.id.category);
        categoryTv.setFocusable(true);
        categoryTv.setFocusableInTouchMode(true);
        categoryTv.setText("Posting for " + category.getName());
        app_bar.setExpanded(true);
        // Date
        mDisplayedDate = findViewById(R.id.date);
        RelativeLayout selectDate = findViewById(R.id.selectDate);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog dialog = new DatePickerDialog(CreatePost.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        mDatesetlistener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface d) {
                        if (dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE) != null) {
                            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                        } else {
                            Log.e(TAG, "Date Picker Negative Button is Null");
                        }
                        if (dialog.getButton(DatePickerDialog.BUTTON_POSITIVE) != null) {
                            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                        } else {
                            Log.e(TAG, "Date Picker Positive Button is Null");
                        }
                    }
                });
                dialog.show();
            }
        });

        mDatesetlistener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                try {
                    month = month + 1;
                    Log.e(TAG, "onDateSet: mm/dd/yy: " + month + "/" + dayOfMonth + "/" + year);
                    String strDate = month + "/" + dayOfMonth + "/" + year;
                    mDisplayedDate.setText(strDate);
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                    Date d = format.parse(strDate);
                    strDate = new SimpleDateFormat("EEE, dd, MMM yyyy").format(d);
                    mDisplayedDate.setText(strDate);
                } catch (Exception e) {
                    Log.e(TAG, "Date parsing Exception: " + e.getMessage());
                }
            }
        };

        // Time
        mDisplayedTime = findViewById(R.id.time);
        RelativeLayout selectTime = findViewById(R.id.selectTime);
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
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                try {
                    Log.e(TAG, "onTimeSet: hh:mm: " + hourOfDay + ":" + minute);
                    String strTime = hourOfDay + ":" + minute;
                    mDisplayedTime.setText(strTime);
                    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
                    Date d = formatter.parse(strTime);
                    Log.e(TAG, "Time Parsed: " + d.toString());
                    strTime = new SimpleDateFormat("hh:mm aa").format(d);
                    Log.e(TAG, "Time Formatted: " + strTime);
                    mDisplayedTime.setText(strTime);
                } catch (Exception e) {
                    Log.e(TAG, "Time parsing exception: " + e.getMessage());
                }
            }
        };

        // Address
        RelativeLayout selectAddress = findViewById(R.id.selectAddress);
        address = findViewById(R.id.address);
        selectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(CreatePost.this, SelectAddress.class);
                startActivityForResult(it, 10);
            }
        });

        slider = findViewById(R.id.slider);

        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(2000);
    }

    private void saveJob() {
        progress.setVisibility(View.VISIBLE);
        post.setVisibility(View.GONE);
        postObj.setImages(sliderImages);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Jobs");
        String id = reference.push().getKey();
        postObj.setId(id);
        postObj.setUserId(user.getId());
        String strPostedDate = new SimpleDateFormat("EEE, dd, MMM yyyy hh:mm a").format(new Date());
        postObj.setPostedTime(strPostedDate);
        reference.child(postObj.getId()).setValue(postObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progress.setVisibility(View.GONE);
                        post.setVisibility(View.VISIBLE);
                        helpers.showSuccess(CreatePost.this, "JOB POSTED!", "Your job has been posted successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.setVisibility(View.GONE);
                        post.setVisibility(View.VISIBLE);
                        helpers.showError(CreatePost.this, "ERROR!", "Job not posted.\nSomething went wrong.\nPlease try again later,");
                    }
                });
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
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }
                Uri imageFile = data.getData();
                if (imageFile != null) {
                    uploadImage(imageFile);
                }
            }
        }
    }

    private void uploadImage(Uri image) {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Jobs").child(user.getId());
        Calendar calendar = Calendar.getInstance();
        storageReference.child(calendar.getTimeInMillis() + "").putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                            taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.e(TAG, "in OnSuccess " + uri.toString());
                                            TextSliderView textSliderView = new TextSliderView(CreatePost.this);
                                            textSliderView
                                                    .description("")
                                                    .image(uri.toString())
                                                    .setScaleType(BaseSliderView.ScaleType.Fit);

                                            slider.addSlider(textSliderView);
                                            sliderImages.add(uri.toString());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Download Url: " + e.getMessage());
                                            helpers.showError(CreatePost.this, "ERROR!", "Image ot added.\nPlease select your image again.");
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Upload Image Failed: " + e.getMessage());
                        helpers.showError(CreatePost.this, "ERROR!", "Image ot added.\nPlease select your image again.");
                    }
                });
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
