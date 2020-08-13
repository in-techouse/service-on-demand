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
import com.example.servicesondemand.activities.OrderDetail;
import com.example.servicesondemand.activities.PostDetail;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.model.Complain;

import java.util.ArrayList;
import java.util.List;

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.ComplainHolder> {
    private List<Complain> data;
    private Context context;

    public ComplainAdapter(Context c) {
        data = new ArrayList<>();
        context = c;
    }

    public void setData(List<Complain> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComplainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complain, parent, false);
        return new ComplainHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplainHolder holder, int position) {
        final Complain complain = data.get(position);
        holder.complain.setText(complain.getComplain());
        holder.difference.setText(Helpers.calculateDateDifference(complain.getDateTime()));
        holder.dateTime.setText(complain.getDateTime());
        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, OrderDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("complain", complain);
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

    class ComplainHolder extends RecyclerView.ViewHolder {
        TextView complain, dateTime, difference;
        CardView mainCard;
        ComplainHolder(@NonNull View itemView) {
            super(itemView);
            complain = itemView.findViewById(R.id.complain);
            difference = itemView.findViewById(R.id.difference);
            dateTime = itemView.findViewById(R.id.dateTime);
            mainCard = itemView.findViewById(R.id.mainCard);
        }
    }
}
