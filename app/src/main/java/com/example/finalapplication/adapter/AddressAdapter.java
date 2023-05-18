package com.example.finalapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalapplication.R;
import com.example.finalapplication.model.AddressModel;

import java.text.DecimalFormat;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder>{

    Context context;
    List<AddressModel> addressModelList;
    SelectedAddress selectedAddress;

    int totalAmount = 0;

    private RadioButton selectedRadioBtn;

    public AddressAdapter(Context context, List<AddressModel> addressModelList, SelectedAddress selectedAddress) {
        this.context = context;
        this.addressModelList = addressModelList;
        this.selectedAddress = selectedAddress;
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.address.setText(addressModelList.get(position).getUserAddress());

        //Total amount to Cart activity
//        totalAmount = totalAmount + addressModelList.get(position).getTotalPrice();
//        Intent intent = new Intent("MyTotalMountAddress");
//        intent.putExtra("totalAmountAddress",totalAmount);
//
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(AddressModel address:addressModelList){
                    address.setSelected(false);
                }
                addressModelList.get(position).setSelected(true);

                if (selectedRadioBtn!=null){
                    selectedRadioBtn.setChecked(false);
                }
                selectedRadioBtn = (RadioButton) view;
                selectedRadioBtn.setChecked(true);
                selectedAddress.setAddress(addressModelList.get(position).getUserAddress());
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView address, totalPrice;
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address_add);
            radioButton = itemView.findViewById(R.id.select_address);
        }
    }

    public interface SelectedAddress {
        void setAddress(String address);
    }
}
