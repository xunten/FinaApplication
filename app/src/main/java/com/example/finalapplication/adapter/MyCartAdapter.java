package com.example.finalapplication.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalapplication.R;
import com.example.finalapplication.activity.MainActivity;
import com.example.finalapplication.model.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    Context context;
    List<MyCartModel> list;
    int totalAmount = 0;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public MyCartAdapter(Context context, List<MyCartModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(list.get(position).getProductImage()).into(holder.productImage);
        holder.productName.setText(list.get(position).getProductName());
        holder.totalQuantity.setText(list.get(position).getTotalQuantity()+"");
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.productPrice.setText(decimalFormat.format(list.get(position).getProductPrice()) + " VND");
        holder.totalPrice.setText(decimalFormat.format(list.get(position).getTotalPrice()) + " VND");

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("User").whereEqualTo("productId", list.get(position).getProductId())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                                        builder.setTitle("Warning: ");
                                        builder.setMessage("Are you sure? Delete this product.");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                doc.getReference().delete();
                                                Toast.makeText(view.getContext(), "DONE", Toast.LENGTH_SHORT).show();
                                                context.startActivity(new Intent(context, MainActivity.class));
                                                ((Activity) context).finish();
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        builder.show();
                                    }
                                }
                            }
                        });
            }
        });

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("User").whereEqualTo("productId", list.get(position).getProductId())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                        doc.getReference().update("totalQty", list.get(position).getTotalQuantity() + 1);
                                        doc.getReference().update("totalPrice", list.get(position).getTotalPrice() + list.get(position).getProductPrice());
                                        list.get(position).setTotalQuantity(list.get(position).getTotalQuantity() + 1);
                                        list.get(position).setTotalPrice(list.get(position).getTotalPrice() + list.get(position).getProductPrice());
                                        notifyItemChanged(position);
                                    }
                                }
                            }
                        });
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("User").whereEqualTo("productId", list.get(position).getProductId())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                        if(list.get(position).getTotalQuantity() > 1){
                                            doc.getReference().update("totalQty", list.get(position).getTotalQuantity() - 1);
                                            doc.getReference().update("totalPrice", list.get(position).getTotalPrice() - list.get(position).getProductPrice());
                                            list.get(position).setTotalQuantity(list.get(position).getTotalQuantity() - 1);
                                            list.get(position).setTotalPrice(list.get(position).getTotalPrice() - list.get(position).getProductPrice());
                                            notifyItemChanged(position);
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                                            builder.setTitle("Warning: ");
                                            builder.setMessage("Are you sure? Delete this product.");
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    doc.getReference().delete();
                                                    context.startActivity(new Intent(context, MainActivity.class));
                                                    ((Activity) context).finish();
                                                    Toast.makeText(view.getContext(), "DONE", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            builder.show();
                                        }
                                    }
                                }
                            }
                        });
            }
        });

        //Total amount to Cart activity
        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            int totalNew = 0;
                            for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                MyCartModel cart = doc.toObject(MyCartModel.class);
                                totalNew = totalNew + cart.getTotalPrice();
                            }
                            totalAmount = totalNew;
                            Intent intent = new Intent("MyTotalMount");
                            intent.putExtra("totalAmount",totalAmount);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, totalPrice, totalQuantity;
        ImageView productImage, remove, minus, add;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.item_cart_image);
            productName = itemView.findViewById(R.id.item_cart_name);
            productPrice = itemView.findViewById(R.id.item_cart_price);
            totalPrice = itemView.findViewById(R.id.item_cart_total_price);
            totalQuantity = itemView.findViewById(R.id.item_cart_quantity);
            remove = itemView.findViewById(R.id.remove);
            minus = itemView.findViewById(R.id.item_cart_minus);
            add = itemView.findViewById(R.id.item_cart_plus);
        }
    }
}
