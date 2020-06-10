package com.example.servicesondemand.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicesondemand.R;
import com.example.servicesondemand.activities.PostDetail;
import com.example.servicesondemand.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private List<Notification> data;
    private int type;
    private Context context;

    public NotificationAdapter(int t, Context c) {
        data = new ArrayList<>();
        type = t;
        context = c;
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
        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, PostDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("notification", notification);
                it.putExtras(bundle);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class NotificationHolder extends RecyclerView.ViewHolder {
        TextView notification, dateTime;
        CardView mainCard;

        NotificationHolder(@NonNull View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification);
            dateTime = itemView.findViewById(R.id.dateTime);
            mainCard = itemView.findViewById(R.id.mainCard);
        }
    }
}
