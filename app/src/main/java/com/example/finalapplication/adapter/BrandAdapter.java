package com.example.finalapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalapplication.R;
import com.example.finalapplication.activity.ShowAllActivity;
import com.example.finalapplication.model.BrandModel;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {

    private Context context;
    private List<BrandModel> list;

    public BrandAdapter(Context context, List<BrandModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BrandAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BrandAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.brandPic);
        holder.brandName.setText(list.get(position).getBrandName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowAllActivity.class);
                intent.putExtra("brand",list.get(position).getBrandName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView brandName;
        ImageView brandPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            brandName = itemView.findViewById(R.id.brandName);
            brandPic = itemView.findViewById(R.id.brandPic);
        }
    }
}
