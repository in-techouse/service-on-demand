package com.example.servicesondemand.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.servicesondemand.R;
import com.example.servicesondemand.activities.PostDetail;
import com.example.servicesondemand.model.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private List<Post> data;
    private Context context;

    public PostAdapter(Context c) {
        data = new ArrayList<>();
        context = c;
    }

    public void setData(List<Post> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        final Post post = data.get(position);
        Glide.with(context).load(post.getImages().get(0)).into(holder.image);
        holder.category.setText(post.getCategory());
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Date d = format.parse(post.getDate());
            String formattedDate = new SimpleDateFormat("EEE, dd/MM/yyyy").format(d);
            holder.date.setText(formattedDate);
        } catch (Exception e) {
            holder.date.setText(post.getDate());
        }
        holder.time.setText(post.getTime());
        holder.offers.setText(post.getOffers().size() + "");

        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, PostDetail.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", post);
                it.putExtras(bundle);
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView category, date, time, offers;
        CardView mainCard;

        PostHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            category = itemView.findViewById(R.id.category);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            offers = itemView.findViewById(R.id.offers);
            mainCard = itemView.findViewById(R.id.mainCard);
        }
    }
}
