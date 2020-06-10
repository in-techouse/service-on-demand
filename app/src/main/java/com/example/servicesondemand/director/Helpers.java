package com.example.servicesondemand.director;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.servicesondemand.R;
import com.example.servicesondemand.model.Notification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Helpers {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications");

    public boolean isConnected(Context context) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }


    public void showError(Activity activity, String title, String message) {
        MaterialDialog mDialog = new MaterialDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", R.drawable.ok, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", R.drawable.cancel, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show Dialog
        mDialog.show();
    }

    public void showErrorWithClose(final Activity activity, String title, String message) {
        MaterialDialog mDialog = new MaterialDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", R.drawable.ok, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        activity.finish();
                    }
                })
                .setNegativeButton("CANCEL", R.drawable.cancel, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        activity.finish();
                    }
                })
                .build();

        // Show Dialog
        mDialog.show();
    }

    public void showSuccess(final Activity activity, String title, String message) {
        MaterialDialog mDialog = new MaterialDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", R.drawable.ok, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        activity.finish();
                    }
                })
                .setNegativeButton("CANCEL", R.drawable.cancel, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        activity.finish();
                    }
                })
                .build();

        // Show Dialog
        mDialog.show();
    }

    public void sendNotification(String userId, String userText, String workerId, String workerText, String jobId) {
        Notification notification = new Notification();
        String id = reference.push().getKey();
        notification.setId(id);
        notification.setUserId(userId);
        notification.setUserText(userText);
        notification.setWorkerId(workerId);
        notification.setWorkerText(workerText);
        notification.setJobId(jobId);
        Calendar calendar = Calendar.getInstance();
        String strDateTime = new SimpleDateFormat("EEE, dd, MMM yyyy hh:mm a").format(calendar.getTime());
        Log.e("Notification", "Notification Date time is: " + strDateTime);
        notification.setDateTime(strDateTime);
        reference.child(notification.getId()).setValue(notification);
    }

    public void showNotification(Activity activity, String text, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "1");
        builder.setTicker(text);
        builder.setAutoCancel(true);
        builder.setChannelId("1");
        builder.setContentInfo(text);
        builder.setContentTitle(text);
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        builder.build();
        NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(10, builder.build());
        }
    }
}
