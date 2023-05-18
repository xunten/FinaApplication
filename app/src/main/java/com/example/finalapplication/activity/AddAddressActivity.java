package com.example.finalapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    EditText address,city,postalCode;
    Toolbar toolbarAddress;
    Button addAddressBtn;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        toolbarAddress=findViewById(R.id.add_address_toolbar);
        setSupportActionBar(toolbarAddress);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarAddress.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        address = findViewById(R.id.ad_address);
        city = findViewById(R.id.ad_city);
        postalCode = findViewById(R.id.ad_code);
        addAddressBtn = findViewById(R.id.ad_add_address);

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userAddress = address.getText().toString();
                String userCity = city.getText().toString();
                String userCode = postalCode.getText().toString();

                String final_address = "";

                if (!userCode.isEmpty()){
                    final_address+=userCode + ", ";
                }

                if (!userAddress.isEmpty()){
                    final_address+=userAddress + ", ";
                }

                if (!userCity.isEmpty()){
                    final_address+=userCity;
                }


                if (!userCode.isEmpty() && !userAddress.isEmpty() && !userCity.isEmpty()){

                    Map<String,String> map = new HashMap<>();
                    map.put("userAddress", final_address);

                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                            .collection("Address").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddAddressActivity.this, "Address Added", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AddAddressActivity.this, CartActivity.class));
                                        finish();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(AddAddressActivity.this, "Kindly Fill All Field", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}