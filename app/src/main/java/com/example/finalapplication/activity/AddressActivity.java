package com.example.finalapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import androidx.appcompat.widget.Toolbar;

import com.example.finalapplication.R;
import com.example.finalapplication.adapter.AddressAdapter;
import com.example.finalapplication.model.AddressModel;
import com.example.finalapplication.model.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress {

    TextView allTotalAmount;
    TextView name, phone, email;
    Button addAddress, checkOutBtn;
    RecyclerView recyclerViewAddress;
    private List<AddressModel> addressModelList;
    private AddressAdapter addressAdapter;

    Toolbar toolbar;
    String mAddress = "";
    int totalAmount;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        totalAmount = getIntent().getIntExtra("totalAmount",0);

        toolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Set data into form of address activity
        name = findViewById(R.id.tvNameCheckout);
        name.setText(auth.getCurrentUser().getDisplayName());

        email = findViewById(R.id.tvEmailCheckout);
        email.setText(auth.getCurrentUser().getEmail());

        phone = findViewById(R.id.tvPhoneCheckout);
        final String[] mobile_phone = {""};
        String mobile;
        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("Phone").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                mobile_phone[0] = doc.getString("userPhone");
                            }
                            phone.setText(mobile_phone[0]);
                        }
                    }
                });

        allTotalAmount = findViewById(R.id.tvTotalAddress);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        allTotalAmount.setText(decimalFormat.format(totalAmount)+" VND");

        recyclerViewAddress = findViewById(R.id.address_recycler);
        addAddress = findViewById(R.id.add_address_btn);

        recyclerViewAddress.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addressModelList = new ArrayList<>();
        addressAdapter = new AddressAdapter(getApplicationContext(),addressModelList,this);
        recyclerViewAddress.setAdapter(addressAdapter);

        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("Address").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc :task.getResult().getDocuments()) {
                                AddressModel addressModel = doc.toObject(AddressModel.class);
                                addressModelList.add(addressModel);
                                addressAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        //get data from my cart adapter
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter("MyTotalMountAddress"));

        checkOutBtn = findViewById(R.id.btnCheckOut);
        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orderName = name.getText().toString();
                String orderPhone = phone.getText().toString();
                String orderEmail = email.getText().toString();
                String orderAddress = mAddress;
                int orderTotal = totalAmount;

                String saveCurrentDate;
                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd MMMM yyyy");
                saveCurrentDate = currentDate.format(calForDate.getTime());

                final String docId;
                docId = firestore.collection("Orders").document().getId();
                //Put user information order to database
                final HashMap<String, Object> cartMap = new HashMap<>();
                cartMap.put("name", orderName);
                cartMap.put("phone", orderPhone);
                cartMap.put("email", orderEmail);
                cartMap.put("address", orderAddress);
                cartMap.put("total", orderTotal);
                cartMap.put("date", saveCurrentDate);
                cartMap.put("status", "Dang chuan bi hang!");
                cartMap.put("id", docId);


                firestore.collection("Orders").document(docId).set(cartMap);

                //Get product from cart
                //then, put product order to database
                firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                        MyCartModel myCartModel = doc.toObject(MyCartModel.class);

                                        final HashMap<String, Object> proMap = new HashMap<>();
                                        proMap.put("proName", myCartModel.getProductName());
                                        proMap.put("proPrice", myCartModel.getProductPrice());
                                        proMap.put("proQty", myCartModel.getTotalQuantity());
                                        proMap.put("proImg", myCartModel.getProductImage());
                                        proMap.put("totalPrice", myCartModel.getTotalPrice());

                                        firestore.collection("Orders").document(docId).collection("products")
                                                .add(proMap);
                                    }
                                }
                            }
                        });

                //Delete cart
                firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                        doc.getReference().delete();
                                    }
                                }
                            }
                        });

                Toast.makeText(AddressActivity.this, "Ordered successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddressActivity.this, MainActivity.class));
                finish();

            }
        });

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddressActivity.this,AddAddressActivity.class));
            }
        });
    }

    @Override
    public void setAddress(String address) {

        mAddress = address;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            int totalBill = intent.getIntExtra("totalAmountAddress", 0);
            allTotalAmount.setText(decimalFormat.format(totalBill)+" VND");
        }
    };
}