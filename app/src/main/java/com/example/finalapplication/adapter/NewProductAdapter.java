package com.example.finalapplication.adapter;

import android.annotation.SuppressLint;
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
import com.example.finalapplication.activity.DetailActivity;
import com.example.finalapplication.model.NewProductModel;

import java.text.DecimalFormat;
import java.util.List;

public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.ViewHolder> {

    private Context context;
    private List<NewProductModel> list;

    public NewProductAdapter(Context context, List<NewProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NewProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_new, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewProductAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.item_pro_image);
        holder.item_pro_description.setText(list.get(position).getDescription());
        holder.item_pro_name.setText(list.get(position).getName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.item_pro_price.setText(decimalFormat.format(list.get(position).getPrice()) + " VND");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("detail",list.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView item_pro_image;
        TextView item_pro_name, item_pro_description, item_pro_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_pro_image = itemView.findViewById(R.id.item_pro_image);
            item_pro_name = itemView.findViewById(R.id.item_pro_name);
            item_pro_description = itemView.findViewById(R.id.item_pro_description);
            item_pro_price = itemView.findViewById(R.id.item_pro_price);
        }
    }
}
