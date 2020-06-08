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
import com.example.servicesondemand.activities.OfferDetail;
import com.example.servicesondemand.activities.OfferVenderDetail;
import com.example.servicesondemand.model.Offer;
import com.example.servicesondemand.model.Post;

import java.util.ArrayList;
import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferHolder> {
    private List<Offer> data;
    private Context context;
    private int type;
    private Post post;

    public OfferAdapter(Context c, int t, Post p) {
        data = new ArrayList<>();
        context = c;
        type = t;
        post = p;
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
        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it;
                if (type == 1)
                    it = new Intent(context, OfferVenderDetail.class);
                else
                    it = new Intent(context, OfferDetail.class);
                Bundle b = new Bundle();
                b.putSerializable("offer", offer);
                b.putSerializable("post", post);
                it.putExtras(b);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
            }
        });
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
            mainCard = itemView.findViewById(R.id.offer_item);
        }
    }

}
