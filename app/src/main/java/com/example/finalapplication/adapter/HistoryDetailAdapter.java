package com.example.finalapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.finalapplication.R;
import com.example.finalapplication.model.HistoryProductModel;

import java.util.List;

public class HistoryDetailAdapter extends RecyclerView.Adapter<HistoryDetailAdapter.ViewHolder>{
    Context context;
    List<HistoryProductModel> list;

    public HistoryDetailAdapter(Context context, List<HistoryProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryProductModel item = list.get(position);
        holder.item_name.setText(item.getProName() + " ");
        holder.item_qty.setText("Qty: "+item.getProQty());
        holder.item_price.setText("Price: "+item.getProPrice());
        Glide.with(context).load(item.getProImg()).into(holder.item_img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView item_img;
        TextView item_name, item_qty, item_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_img = itemView.findViewById(R.id.item_proImgDetail);
            item_name = itemView.findViewById(R.id.item_proNameDetail);
            item_qty = itemView.findViewById(R.id.item_proQtyDetail);
            item_price = itemView.findViewById(R.id.item_proPriceDetail);
        }
    }
}
