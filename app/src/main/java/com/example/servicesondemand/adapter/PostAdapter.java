package com.example.servicesondemand.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicesondemand.model.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private List<Post> data;
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class PostHolder extends RecyclerView.ViewHolder{

        public PostHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
