package com.example.servicesondemand.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import java.util.concurrent.TimeUnit;

public class GetStarted extends AppCompatActivity
{
    Button home;
    private EditText InputUserPhoneNumber, InputUserVerificationCode;
    private Button SendVerificationCodeButton, VerifyButton;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Helpers helpers;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstart);
        home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetStarted.this, Home.class);
                 startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();


        InputUserPhoneNumber = (EditText) findViewById(R.id.phone_number_input);
        InputUserVerificationCode = (EditText) findViewById(R.id.verification_code_input);
        SendVerificationCodeButton = (Button) findViewById(R.id.send_ver_code_button);
        VerifyButton = (Button) findViewById(R.id.verify_button);
        loadingBar = new ProgressDialog(this);

        helpers=new Helpers();


        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = helpers.isConnected(GetStarted.this);
                if (!flag) {
                    helpers.showError(GetStarted.this,"ËRROR","NO INTERNET CONNECTION FOUND PLEASE CHECK INTERNET");
                    return;
                }


                String phoneNumber = InputUserPhoneNumber.getText().toString();

                if (phoneNumber.length() !=13)
                {
                    InputUserPhoneNumber.setError("Enter a valid phone number");
                }
                else
                {
                    InputUserPhoneNumber.setError(null);
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait, while we are authenticating using your phone...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, GetStarted.this, callbacks);
                }
            }
        });



        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                InputUserPhoneNumber.setVisibility(View.INVISIBLE);
                SendVerificationCodeButton.setVisibility(View.INVISIBLE);


                String verificationCode = InputUserVerificationCode.getText().toString();

                if (TextUtils.isEmpty(verificationCode))
                {
                    Toast.makeText(GetStarted.this, "Please write verification code first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Verification Code");
                    loadingBar.setMessage("Please wait, while we are verifying verification code...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                Toast.makeText(GetStarted.this, "Invalid Phone Number, Please enter correct phone number with your country code..." +  e.getMessage(), Toast.LENGTH_LONG).show();
                loadingBar.dismiss();

                InputUserPhoneNumber.setVisibility(View.VISIBLE);
                SendVerificationCodeButton.setVisibility(View.VISIBLE);

                InputUserVerificationCode.setVisibility(View.INVISIBLE);
                VerifyButton.setVisibility(View.INVISIBLE);
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token)
            {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;


                Toast.makeText(GetStarted.this, "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

                InputUserPhoneNumber.setVisibility(View.INVISIBLE);
                SendVerificationCodeButton.setVisibility(View.INVISIBLE);

                InputUserVerificationCode.setVisibility(View.VISIBLE);
                VerifyButton.setVisibility(View.VISIBLE);
            }
        };
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(GetStarted.this, "Congratulations, you're logged in Successfully.", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(GetStarted.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(GetStarted.this, Home.class);
        startActivity(mainIntent);
        finish();
    }

}
