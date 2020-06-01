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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class CreateUserProfile extends AppCompatActivity {
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private EditText InputUserFirstName;
    private EditText InputUserLastName;
    private EditText InputUserEmail;
    private String strUserFirstName, strUserLastName, strUserEmail, strUserPhoneNumber;
    private Helpers helpers;
    private Uri imagePath;
    private User user;
    private Session session;
    private ProgressDialog loadingBar;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);

        Intent it = getIntent();
        if (it == null) { //agar bundle mahi mila to
            finish(); //jab data wapis mile ga to reverse order mai mile ga thas y use finish
            return;
        }

        Bundle bundle = it.getExtras();  //bundle k ander jo data tha os ko access krne k liye it.getExtras() use this
        if (bundle == null) {
            finish();
            return;
        }

        strUserPhoneNumber = bundle.getString("phone");
        if (strUserPhoneNumber == null) {
            finish();
            return;
        }


        Button submit = findViewById(R.id.submit);
        InputUserFirstName = findViewById(R.id.first_name_input);
        InputUserLastName = findViewById(R.id.last_name_input);
        InputUserEmail = findViewById(R.id.email_input);
        EditText inputUserPhoneNumber = findViewById(R.id.phone_number_input);

        inputUserPhoneNumber.setText(strUserPhoneNumber);

        image = findViewById(R.id.image);
        image.setImageDrawable(getResources().getDrawable(R.drawable.user));

        helpers = new Helpers();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = helpers.isConnected(CreateUserProfile.this);
                if (!flag) {
                    helpers.showError(CreateUserProfile.this, "ERROR", "NO INTERNET CONNECTION FOUND PLEASE CHECK INTERNET");
                    return;
                }
                boolean isFlag = isValid();

                if (isFlag) {
                    // Everything is valid
                    loadingBar.setTitle("SAVING");
                    loadingBar.setMessage("Please wait, while we are saving your profile...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    if (imagePath == null) {
                        user.setImage("");
                        saveToDatabase();    //function call to save data in database
                    } else {
                        uploadImage();     //function call to upload image
                    }
                }
            }
        });

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = hasPermissions(CreateUserProfile.this, PERMISSIONS); //jab b user k mobile ki personal chiz ko access karna ho permission ki need hoti hai
                if (!flag) {
                    ActivityCompat.requestPermissions(CreateUserProfile.this, PERMISSIONS, 1);
                } else {
                    openGallery();
                }
            }
        });

        session = new Session(CreateUserProfile.this);
        user = new User();
        user.setPhone(strUserPhoneNumber);

        loadingBar = new ProgressDialog(this);

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
                                        helpers.showError(CreateUserProfile.this, "ERROR!", "Something went wrong.\n Please try again later.");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Profile", "Upload Image Url: " + e.getMessage());
                        loadingBar.dismiss();
                        helpers.showError(CreateUserProfile.this, "ERROR!", "Something went wrong.\n Please try again later.");
                    }
                });
    }

    private void saveToDatabase() {
        user.setCategory("");
        user.setEmail(strUserEmail);
        user.setFirstName(strUserFirstName);
        user.setId(strUserPhoneNumber);
        user.setLastName(strUserLastName);
        user.setType(0);              // we pass 0 en sab mai kyu k user es ko kabhi change nahi kare ga
        user.setPerHourCharge(0);
        user.setRating(0);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child("Users").child(strUserPhoneNumber).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingBar.dismiss();
                        session.setSession(user);
                        Intent it = new Intent(CreateUserProfile.this, CustomerDashboard.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(it);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        helpers.showError(CreateUserProfile.this, "ERROR!", "Something went wrong.\n Please try again later.");
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

        strUserFirstName = InputUserFirstName.getText().toString();
        strUserLastName = InputUserLastName.getText().toString();
        strUserEmail = InputUserEmail.getText().toString();

        if (strUserFirstName.length() < 3) {
            InputUserFirstName.setError("Enter a valid first name");
            flag = false;
        } else {
            InputUserFirstName.setError(null);
        }

        if (strUserLastName.length() < 3) {
            InputUserLastName.setError("Enter a valid last name");
            flag = false;
        } else {
            InputUserLastName.setError(null);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(strUserEmail).matches()) {
            InputUserEmail.setError("Enter a valid email");
            flag = false;
        } else {
            InputUserEmail.setError(null);
        }
        return flag;
    }

}


