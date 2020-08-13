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
import java.util.Date;

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

    public static String calculateDateDifference(String dateTime) {
        try {
            Log.e("DateTime", "Actual String Date time: " + dateTime);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd, MMM yyyy hh:mm a");
            Date date = sdf.parse(dateTime);
            Log.e("DateTime", "Actual: " + date.toString());
            long estDateInLong = date.getTime();
            long currentTimeInLong = Calendar.getInstance().getTimeInMillis();
            long diff = (long) (currentTimeInLong - estDateInLong);
            long diffDay = diff / (24 * 60 * 60 * 1000);
            if (diffDay > 0) {
                return diffDay + " Days ago";
            } else {
                diff = diff - (diffDay * 24 * 60 * 60 * 1000);
                long diffHours = diff / (60 * 60 * 1000);
                if (diffHours > 0) {
                    return diffHours + " Hours ago";
                } else {
                    diff = diff - (diffHours * 60 * 60 * 1000);
                    long diffMinutes = diff / (60 * 1000);
                    if (diffMinutes > 0) {
                        return diffMinutes + " Minutes ago";
                    } else {
                        diff = diff - (diffMinutes * 60 * 1000);
                        long diffSeconds = diff / 1000;
                        return diffSeconds + " Seconds ago";
                    }
                }
            }
        } catch (Exception e) {
            Log.e("DateTime", "Exception: " + e.getMessage());
            return "";
        }
    }
}
