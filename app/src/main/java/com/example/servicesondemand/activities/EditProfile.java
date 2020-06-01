package com.example.servicesondemand.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private TextInputLayout perHourChargeUpper, categoryUpper;
    private EditText firstName, lastName, email, mobile, category, perHourCharge;
    private String strFirstName, strLastName, strEmail, strPerHourCharge;
    private Button update;
    private Helpers helpers;
    private Session session;
    private User user;
    private ImageView image;
    private Uri imagePath;
    private ProgressDialog loadingBar;

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
        loadingBar = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.update: {
                if (helpers.isConnected(getApplicationContext())) {
                    if (isValid()) {
                        // Everything is valid
                        loadingBar.setTitle("SAVING");
                        loadingBar.setMessage("Please wait, while we are saving your profile...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.setCancelable(false);
                        loadingBar.show();
                        if (imagePath == null) {
                            saveToDatabase();    //function call to save data in database
                        } else {
                            uploadImage();     //function call to upload image
                        }
                    }
                } else {
                    helpers.showError(EditProfile.this, "ERROR!", "No internet connection found.\nConnect to a network and try again.");
                }
                break;
            }
            case R.id.fab: {
                boolean flag = hasPermissions(EditProfile.this, PERMISSIONS); //jab b user k mobile ki personal chiz ko access karna ho permission ki need hoti hai
                if (!flag) {
                    ActivityCompat.requestPermissions(EditProfile.this, PERMISSIONS, 1);
                } else {
                    openGallery();
                }
                break;
            }
        }
    }

    private void uploadImage() {                       // .getReference().child("Users")that mean u are making folder of user or next child show folder of getPhone
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(user.getPhone());
        Calendar calendar = Calendar.getInstance(); //user time stamps ek ka matlan ek file ka naam jo ek dfa rakh dia jae wo dobara nahi aye ga        Log.e("profile", "selected Path " + imagePath.toString());
        storageReference.child(calendar.getTimeInMillis() + "").putFile(imagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {// +"" ka matlab hai concatination
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {//file cloud par upload krne k baad ab hume os ka reference chahiye
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.e("Profile", "in OnSuccess " + uri.toString());//jo UIrecieve hua os ko save karwaya
                                        user.setImage(uri.toString());
                                        saveToDatabase();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Profile", "Download Url: " + e.getMessage());
                                        loadingBar.dismiss();
                                        helpers.showError(EditProfile.this, "ERROR!", "Something went wrong.\n Please try again later.");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Profile", "Upload Image Url: " + e.getMessage());
                        loadingBar.dismiss();
                        helpers.showError(EditProfile.this, "ERROR!", "Something went wrong.\n Please try again later.");
                    }
                });
    }

    private void saveToDatabase() {
        user.setEmail(strEmail);
        user.setFirstName(strFirstName);
        user.setLastName(strLastName);
        if (user.getType() == 0) {
            user.setCategory("");
            user.setPerHourCharge(0);
        } else {
            user.setPerHourCharge(Integer.parseInt(strPerHourCharge));
        }
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child("Users").child(user.getPhone()).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingBar.dismiss();
                        session.setSession(user);
                        if (user.getType() == 0) {
                            Intent it = new Intent(EditProfile.this, CustomerDashboard.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(it);
                            finish();
                        } else {
                            Intent it = new Intent(EditProfile.this, VendorDashboard.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(it);
                            finish();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        helpers.showError(EditProfile.this, "ERROR!", "Something went wrong.\n Please try again later.");
                    }
                });
    }

    private boolean hasPermissions(Context c, String... permission) {     //loop chlae ga kya sab chizon ki jis jis ki access chahiye thi kya majod hai
        for (String p : permission) {
            if (ActivityCompat.checkSelfPermission(c, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2); //use to open gallery
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            openGallery();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Profile", "Gallery Call Back Received in Fragment with Request Code: " + requestCode);
        if (requestCode == 2) {
            Log.e("Profile", "Inside first if");
            if (resultCode == RESULT_OK) {
                Log.e("Profile", "Inside second if");
                if (data != null) {
                    Log.e("Profile", "Data is not null");
                    Uri img = data.getData();
                    if (img != null) {
                        Log.e("Profile", "Img is not null");
                        Glide.with(getApplicationContext()).load(img).into(image);
                        imagePath = img;
                    }
                }
            }
        }
    }

    private boolean isValid() {
        boolean flag = true;
        strFirstName = firstName.getText().toString();
        strLastName = lastName.getText().toString();
        strPerHourCharge = perHourCharge.getText().toString();
        strEmail = email.getText().toString();
        if (strFirstName.length() < 3) {
            firstName.setError("Enter a valid name");
            flag = false;
        }
        if (strLastName.length() < 3) {
            lastName.setError("Enter a valid name");
            flag = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            email.setError("Enter a valid email");
            flag = false;
        }
        if (strPerHourCharge.length() < 1) {
            perHourChargeUpper.setError("Enter a valid per hour charge.");
        }
        return flag;
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
