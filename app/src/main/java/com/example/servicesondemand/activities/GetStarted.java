package com.example.servicesondemand.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class GetStarted extends AppCompatActivity {
    private PinView otpPinView;
    private LinearLayout otpMain, main;
    private EditText InputUserPhoneNumber;
    private Button SendVerificationCodeButton;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
    private ValueEventListener valueEventListener;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Helpers helpers;
    private boolean isCodeSent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstart);


        mAuth = FirebaseAuth.getInstance();


        InputUserPhoneNumber = (EditText) findViewById(R.id.phone_number_input);
        SendVerificationCodeButton = (Button) findViewById(R.id.send_ver_code_button);
        otpPinView = findViewById(R.id.otpPinView);
        otpMain = findViewById(R.id.otpMain);
        main = findViewById(R.id.main);
        loadingBar = new ProgressDialog(this);

        helpers = new Helpers();


        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = helpers.isConnected(GetStarted.this);
                if (!flag) {
                    helpers.showError(GetStarted.this, "ERROR", "NO INTERNET CONNECTION FOUND PLEASE CHECK INTERNET");
                    return;
                }

                if (!isCodeSent) {
                    String phoneNumber = InputUserPhoneNumber.getText().toString();

                    if (phoneNumber.length() != 13) {
                        InputUserPhoneNumber.setError("Enter a valid phone number");
                    } else {
                        InputUserPhoneNumber.setError(null);
                        loadingBar.setTitle("Phone Verification");
                        loadingBar.setMessage("Please wait, while we are authenticating using your phone...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, GetStarted.this, callbacks);
                    }
                } else {
                    String otp = otpPinView.getText().toString();
                    if (otp.length() != 6) {
                        otpPinView.setError("The provided OTP is incorrect");
                    } else {
                        loadingBar.setTitle("Phone Verification");
                        loadingBar.setMessage("Please wait, while we are authenticating using your phone...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                        signInWithPhoneAuthCredential(credential);
                    }
                }
            }
        });


//        VerifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                InputUserPhoneNumber.setVisibility(View.INVISIBLE);
//                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
//
//
//                String verificationCode = InputUserVerificationCode.getText().toString();
//
//                if (TextUtils.isEmpty(verificationCode))
//                {
//                    Toast.makeText(GetStarted.this, "Please write verification code first...", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    loadingBar.setTitle("Verification Code");
//                    loadingBar.setMessage("Please wait, while we are verifying verification code...");
//                    loadingBar.setCanceledOnTouchOutside(false);
//                    loadingBar.show();
//
//                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
//                    sgnInWithPhoneAuthCredential(credential);
//                }
//            }
//        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
//                Toast.makeText(GetStarted.this, "Invalid Phone Number, Please enter correct phone number with your country code..." + e.getMessage(), Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
                helpers.showError(GetStarted.this, "ERROR", e.getMessage());

//                InputUserPhoneNumber.setVisibility(View.VISIBLE);
//                SendVerificationCodeButton.setVisibility(View.VISIBLE);

//                InputUserVerificationCode.setVisibility(View.INVISIBLE);
//                VerifyButton.setVisibility(View.INVISIBLE);
            }

            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                loadingBar.dismiss();

                main.setVisibility(View.GONE);
                otpMain.setVisibility(View.VISIBLE);
                SendVerificationCodeButton.setText("VERIFY");
                isCodeSent = true;
            }
        };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUserDetail();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                helpers.showError(GetStarted.this, "ERROR", e.getMessage());
            }
        });
    }

    private void checkUserDetail() {
        final String phoneNumber = InputUserPhoneNumber.getText().toString();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child(phoneNumber).removeEventListener(valueEventListener);
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Intent intent = new Intent(GetStarted.this, CreateUserProfile.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("phone", InputUserPhoneNumber.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    Session session = new Session(getApplicationContext());
                    session.setSession(user);
                    if (user.getType() == 0) {
                        Intent intent = new Intent(GetStarted.this, CustomerDashboard.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(GetStarted.this, VendorDashboard.class);
                        startActivity(intent);
                    }
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                reference.child(phoneNumber).removeEventListener(valueEventListener);
                loadingBar.dismiss();
                helpers.showError(GetStarted.this, "ERROR", "Something went wrong please try again");
            }
        };
        reference.child(phoneNumber).addValueEventListener(valueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(valueEventListener != null)
            reference.removeEventListener(valueEventListener);
    }
}
