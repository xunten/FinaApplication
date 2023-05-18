package com.example.finalapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalapplication.R;
import com.example.finalapplication.model.HistoryModel;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context context;
    List<HistoryModel> array;

    public HistoryAdapter(Context context, List<HistoryModel> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryModel order = array.get(position);
        holder.idOrder.setText("Order #"+order.getId());
        if(order.getStatus().equals("Đơn hàng đã được thanh toán")) {
            holder.status.setTextColor(Color.parseColor("#FF0D4A10"));
            holder.status.setText(order.getStatus());
        } else if(order.getStatus().equals("Đơn hàng đang được giao")){
            holder.status.setTextColor(Color.parseColor("#008eff"));
            holder.status.setText(order.getStatus());
        } else if(order.getStatus().equals("Đơn hàng đang được xử lí")) {
            holder.status.setTextColor(Color.parseColor("#FF1100"));
            holder.status.setText(order.getStatus());
        } else if(order.getStatus().equals("Đã hủy")) {
            holder.status.setTextColor(Color.parseColor("#FF000000"));
            holder.status.setText(order.getStatus());
        }
        holder.created_at.setText("Order at " + order.getDate());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                holder.rc_history_detail.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        linearLayoutManager.setInitialPrefetchItemCount(order.getProducts().size());

        //Adapter detail order
        HistoryDetailAdapter detailAdapter = new HistoryDetailAdapter(context, order.getProducts());
        holder.rc_history_detail.setLayoutManager(linearLayoutManager);
        holder.rc_history_detail.setAdapter(detailAdapter);
        holder.rc_history_detail.setRecycledViewPool(viewPool);
    }


    @Override
    public int getItemCount() {
        return array.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView idOrder, created_at;
        RecyclerView rc_history_detail;
        Button status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idOrder = itemView.findViewById(R.id.idOrder);
            created_at = itemView.findViewById(R.id.dateOrder);
            status = itemView.findViewById(R.id.status);
            rc_history_detail = itemView.findViewById(R.id.rc_history_detail);
        }
    }
}
