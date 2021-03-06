package com.example.servicesondemand.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private TextView resend, timer;

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
        resend = findViewById(R.id.resend);
        timer = findViewById(R.id.timer);
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

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                helpers.showError(GetStarted.this, "ERROR", e.getMessage());
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
                startTimer();
            }
        };

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = InputUserPhoneNumber.getText().toString();

                if (phoneNumber.length() != 13) {
                    InputUserPhoneNumber.setError("Enter a valid phone number");
                } else {
                    InputUserPhoneNumber.setError(null);
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait, while we are authenticating using your phone...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, GetStarted.this, callbacks, mResendToken);
                }
            }
        });
    }

    private void startTimer() {
        resend.setEnabled(false);
        new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisUntilFinished = millisUntilFinished / 1000;
                long seconds = millisUntilFinished % 60;
                long minutes = (millisUntilFinished / 60) % 60;
                String time = "";
                if (seconds > 9) {
                    time = "0" + minutes + ":" + seconds;
                } else {
                    time = "0" + minutes + ":" + "0" + seconds;
                }
                timer.setText(time);
            }

            @Override
            public void onFinish() {
                timer.setText("--:--");
                resend.setEnabled(true);
            }
        }.start();
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
                    Bundle bundle = new Bundle();// ek activity se dosri activity mai data send krne k liye
                    bundle.putString("phone", InputUserPhoneNumber.getText().toString());//phone k naaam se ek value save karo jis mai phone no save ho ga
                    intent.putExtras(bundle);// data rakha bundle mai bundle rakha intent mai
                    startActivity(intent);
                    finish();
                } else {
                    Session session = new Session(getApplicationContext());
                    session.setSession(user);
                    if (user.getType() == 0) {
                        Intent intent = new Intent(GetStarted.this, CustomerDashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(GetStarted.this, VendorDashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        if (valueEventListener != null)
            reference.removeEventListener(valueEventListener);
    }
}
