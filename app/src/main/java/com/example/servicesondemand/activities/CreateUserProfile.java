package com.example.servicesondemand.activities;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.servicesondemand.director.Helpers;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.servicesondemand.R;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CreateUserProfile extends AppCompatActivity {

    private Button Submit;
    private EditText InputUserFirstName,InputUserLastName,InputUserEmail,InputUserPhoneNumber;
    private String strUserFirstName,strUserLastName,strUserEmail,strUserPhoneNumber;
    private Helpers helpers;
    private ProgressDialog loadingBar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Submit = (Button) findViewById(R.id.submit);
        InputUserFirstName = (EditText) findViewById(R.id.first_name_input);
        InputUserLastName = (EditText) findViewById(R.id.last_name_input);
        InputUserEmail = (EditText) findViewById(R.id.email_input);
        InputUserPhoneNumber = (EditText) findViewById(R.id.phone_number_input);

        helpers = new Helpers();
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = helpers.isConnected(CreateUserProfile.this);
                if (!flag) {
                    helpers.showError(CreateUserProfile.this, "ERROR", "NO INTERNET CONNECTION FOUND PLEASE CHECK INTERNET");
                    return;
                }

                boolean isFlag = isValid();

                if(isFlag){
                    // Everything is valid
                }
                else {
                }

            }
        });

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private boolean isValid(){
        boolean flag = true;

        strUserFirstName = InputUserFirstName.getText().toString();
        strUserLastName = InputUserLastName.getText().toString();
        strUserEmail = InputUserEmail.getText().toString();
        // Reaming

        if(strUserFirstName.length() < 3){
            InputUserFirstName.setError("Enter a valid first name");
            flag = false;
        }
        else{
            InputUserFirstName.setError(null);
        }

        if (strUserLastName.length()<3){
            InputUserLastName.setError("Enter a valid Last name");
            flag = false;
        }
        else{
            InputUserLastName.setError(null);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(strUserEmail).matches()){
            InputUserEmail.setError("Enter a valid Email name");
            flag = false;
        }
        else{
            InputUserEmail.setError(null);
        }
        return flag;
    }

}


