package com.example.finalapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import com.example.finalapplication.model.ShowAllModel;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ShowAllAdapter extends RecyclerView.Adapter<ShowAllAdapter.ViewHolder> {

    private Context context;
    private List<ShowAllModel> list;

    public ShowAllAdapter(Context context, List<ShowAllModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ShowAllAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_all, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ShowAllAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(list.get(position).getImg_url()).into(holder.showItemImage);
        holder.showItemName.setText(list.get(position).getName());
        holder.showItemBrand.setText(list.get(position).getType().toUpperCase(Locale.ROOT));
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.showItemPrice.setText(decimalFormat.format(list.get(position).getPrice()) + " VND");

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

        private ImageView showItemImage;
        private TextView showItemPrice;
        private TextView showItemName, showItemBrand;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showItemImage = itemView.findViewById(R.id.item_pro_image);
            showItemName = itemView.findViewById(R.id.item_pro_name);
            showItemBrand = itemView.findViewById(R.id.item_pro_brand);
            showItemPrice = itemView.findViewById(R.id.item_pro_price);

        }
    }
}
