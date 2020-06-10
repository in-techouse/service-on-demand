package com.example.servicesondemand.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.example.servicesondemand.R;
import com.example.servicesondemand.activities.OrderDetail;

public class JobCompleteDialog extends Dialog implements View.OnClickListener {
    private EditText amount;
    private AppCompatButton submit;
    private OrderDetail orderDetail;

    public JobCompleteDialog(@NonNull Activity activity, OrderDetail or) {
        super(activity);
        orderDetail = or;
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.job_complete_dialog);

        amount = findViewById(R.id.amount);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.submit: {
                String strAmount = amount.getText().toString();
                if (strAmount.length() < 2) {
                    amount.setError("Enter a valid amount");
                    return;
                }
                orderDetail.markJobComplete(Integer.parseInt(strAmount));
                dismiss();
                break;
            }
        }
    }
}
