package com.example.servicesondemand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.servicesondemand.R;
import com.example.servicesondemand.model.Post;

import java.util.ArrayList;
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
        holder.date.setText(post.getDate());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView category, date;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            category = itemView.findViewById(R.id.category);
            date = itemView.findViewById(R.id.date);
        }
    }
}
