package com.example.servicesondemand.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.model.Complain;
import com.example.servicesondemand.model.Offer;
import com.example.servicesondemand.model.Post;

public class SubmitComplain extends AppCompatActivity {

    private TextView submitcomplain;
    private Button submit;
    private Helpers helpers;
    private Complain complain;
    private String strsubmitcomplain;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_complain);


        submitcomplain = findViewById(R.id.submitcomplain);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = helpers.isConnected(SubmitComplain.this);
                if (!isConnected) {
                    helpers.showError(SubmitComplain.this, "ERROR", "NO INTERNET CONNECTION FOUND PLEASE TRY AGAIN LATER");
                    return;
                }
                strsubmitcomplain = submitcomplain.getText().toString();
                if (strsubmitcomplain.length() < 10) {
                    submitcomplain.setError("Enter a valid complain");
                    flag = false;
                }



            }
        });
    }
}