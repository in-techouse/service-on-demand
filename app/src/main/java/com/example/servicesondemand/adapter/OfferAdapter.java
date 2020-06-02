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

import com.bumptech.glide.Glide;
import com.example.servicesondemand.R;
import com.example.servicesondemand.activities.OfferDetail;
import com.example.servicesondemand.activities.PostDetail;
import com.example.servicesondemand.model.Offer;
import com.example.servicesondemand.model.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferHolder> {
    private List<Offer> data;
    private Context context;

    public OfferAdapter(Context c) {
        data = new ArrayList<>();
        context = c;
    }

    public void setData(List<Offer> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_item, parent, false);
        return new OfferHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull OfferHolder holder, int position) {

        final Offer offer = data.get(position);
        holder.description.setText(offer.getDescription());
        holder.perHourCharge.setText(offer.getBudgetOffered() + " RS.");
        holder.timerequired.setText(offer.getTimeRequired() + "");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class OfferHolder extends RecyclerView.ViewHolder {

        TextView category, description, timerequired, perHourCharge;
        CardView mainCard;

        OfferHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            timerequired = itemView.findViewById(R.id.timerequired);
            perHourCharge = itemView.findViewById(R.id.perHourCharge);
        }
    }

}
