package com.example.servicesondemand.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.servicesondemand.R;

import java.sql.Time;
import java.util.Calendar;

public class CreatePost extends AppCompatActivity {
    Button post;
    private static final String TAG = "location";
    private TextView mDisplayedDate;
    private TextView mDisplayedTime;
    private DatePickerDialog.OnDateSetListener mDatesetlistener;
    private TimePickerDialog.OnTimeSetListener mTimesetlistener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


            post =(Button)findViewById(R.id.post);
            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(CreatePost.this,"Your booking is done...",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreatePost.this, CreatePost.class);
                    startActivity(intent);
                }
            });

            //date

            mDisplayedDate = (TextView)findViewById(R.id.date);
            mDisplayedDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal= Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month= cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog dialog= new DatePickerDialog(CreatePost.this,
                            android.R.style.Theme_DeviceDefault_Dialog,
                            mDatesetlistener, year,month,day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });

        mDatesetlistener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                Log.d(TAG, "onDateSet: mm/dd/yy: " +month+ "/" +day+ "/" +year);
                String date = month+ "/" +day+ "/" +year;
                mDisplayedDate.setText(date);
            }
        };

        //time
        mDisplayedTime = (TextView)findViewById(R.id.time);
        mDisplayedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal= Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR);
                int minute= cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(CreatePost.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                    }
                }, hour, minute, false);
//                TimePickerDialog dialog= new DatePickerDialog(CreatePost.this, android.R.style.Theme_DeviceDefault_Dialog,
//                        mTimesetlistener ,hour,minute);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mTimesetlistener= new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.d(TAG, "onTimeSet: hh:mm: " +hour+ ":" +minute);
                String time = hour+ ":" +minute ;
                mDisplayedTime.setText(time);
            }
        };

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
