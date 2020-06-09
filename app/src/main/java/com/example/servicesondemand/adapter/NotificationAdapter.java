package com.example.servicesondemand.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicesondemand.R;
import com.example.servicesondemand.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private List<Notification> data;
    private int type;

    public NotificationAdapter(int t) {
        data = new ArrayList<>();
        type = t;
    }

    public void setData(List<Notification> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        final Notification notification = data.get(position);
        if (type == 0) {
            holder.notification.setText(notification.getUserText());
        } else {
            holder.notification.setText(notification.getWorkerText());
        }
        holder.dateTime.setText(notification.getDateTime());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class NotificationHolder extends RecyclerView.ViewHolder {
        TextView notification, dateTime;

        NotificationHolder(@NonNull View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification);
            dateTime = itemView.findViewById(R.id.dateTime);
        }
    }
}
