package com.example.finalapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapplication.R;
import com.example.finalapplication.adapter.MyCartAdapter;
import com.example.finalapplication.model.MyCartModel;
import com.example.finalapplication.model.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    int overAllTotalAmount;
    TextView allTotalAmount, tvTotal;
    TextView emptyCart;
    Toolbar toolbarMyCart;
    RecyclerView recyclerView;
    List<MyCartModel> cartModelList;
    MyCartAdapter cartAdapter;
    Button checkOutBtn;

    boolean isAliveCart = true;

    int totalBill;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toolbarMyCart = findViewById(R.id.toolbar_my_cart);
        tvTotal = findViewById(R.id.tvTotal);
        emptyCart = findViewById(R.id.tvCartEmpty);
        setSupportActionBar(toolbarMyCart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarMyCart.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //get data from my cart adapter
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter("MyTotalMount"));

        checkOutBtn = findViewById(R.id.btnCheckOut);
        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAliveCart) {
                    Intent intent = new Intent(CartActivity.this, AddressActivity.class);
                    intent.putExtra("totalAmount", totalBill);
                    startActivity(intent);
                } else {
                    Toast.makeText(CartActivity.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        allTotalAmount = findViewById(R.id.tvTotal);
        recyclerView = findViewById(R.id.cart_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(this,cartModelList);
        recyclerView.setAdapter(cartAdapter);

        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                emptyCart.setVisibility(View.VISIBLE);
                                isAliveCart = false;
                                return;
                            }
                            toolbarMyCart.setTitle("My Cart (" + task.getResult().size() + ")");
                            for (DocumentSnapshot doc :task.getResult().getDocuments()) {
                                MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                                cartModelList.add(myCartModel);
                                cartAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            totalBill = intent.getIntExtra("totalAmount", 0);
            allTotalAmount.setText(decimalFormat.format(totalBill)+" VND");
        }
    };
}