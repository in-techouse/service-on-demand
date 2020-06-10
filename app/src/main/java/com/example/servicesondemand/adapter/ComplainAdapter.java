package com.example.servicesondemand.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicesondemand.R;
import com.example.servicesondemand.model.Complain;

import java.util.ArrayList;
import java.util.List;

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.ComplainHolder> {
    private List<Complain> data;

    public ComplainAdapter() {
        data = new ArrayList<>();
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
        holder.dateTime.setText(complain.getDateTime());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ComplainHolder extends RecyclerView.ViewHolder {
        TextView complain, dateTime;

        ComplainHolder(@NonNull View itemView) {
            super(itemView);
            complain = itemView.findViewById(R.id.complain);
            dateTime = itemView.findViewById(R.id.dateTime);
        }
    }
}
