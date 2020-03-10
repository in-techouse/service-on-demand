package com.example.servicesondemand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.servicesondemand.R;
import com.example.servicesondemand.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private List<Category> categories;
    private Context context;

    public CategoryAdapter(Context c) {
        context = c;
        categories = new ArrayList<>();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.category_item, null);
        TextView title = v.findViewById(R.id.title);
        ImageView imageView = v.findViewById(R.id.icon);
        title.setText(categories.get(position).getName());
        Glide.with(context).load(categories.get(position).getUrl()).into(imageView);
        return v;
    }
}
