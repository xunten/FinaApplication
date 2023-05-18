package com.example.finalapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalapplication.R;
import com.example.finalapplication.model.MyCartModel;
import com.example.finalapplication.model.NewProductModel;
import com.example.finalapplication.model.PopularProductModel;
import com.example.finalapplication.model.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

    Toolbar toolbarDetail;
    ImageView detailImg, cartImg;
    TextView ratingText, name, description, price, quantity;
    RatingBar ratingBar;
    Button addToCart, buyNow;
    ImageView addItems, removeItems;

    String productImg, productId;
    int productPrice = 0;
    int totalQuantity = 1;
    int totalPrice = 0;
    int priceEnd = 0;
    int qtyEnd=0;

    //New Products
    NewProductModel newProductModel = null;

    //New Products
    PopularProductModel popularProductModel = null;

    //Show All
    ShowAllModel showAllModel = null;

    FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final Object object = getIntent().getSerializableExtra("detail");

        if (object instanceof NewProductModel) {
            newProductModel = (NewProductModel) object;
        } else if (object instanceof PopularProductModel) {
            popularProductModel = (PopularProductModel) object;
        } else if (object instanceof ShowAllModel) {
            showAllModel = (ShowAllModel) object;
        }

        detailImg = findViewById(R.id.detail_img);
        cartImg = findViewById(R.id.cart_img);
        name = findViewById(R.id.detail_name);
        quantity = findViewById(R.id.quantity);
        ratingText = findViewById(R.id.ratingTv);
        description = findViewById(R.id.detail_description);
        price = findViewById(R.id.detail_price);

        addToCart = findViewById(R.id.add_to_cart);
//        buyNow = findViewById(R.id.buy_now);

        addItems = findViewById(R.id.add_item);
        removeItems = findViewById(R.id.remove_item);
        toolbarDetail = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbarDetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //New Products
        if (newProductModel != null) {
            Glide.with(getApplicationContext()).load(newProductModel.getImg_url()).into(detailImg);
            name.setText(newProductModel.getName());
            ratingText.setText(newProductModel.getRating());
            description.setText(newProductModel.getDescription());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            price.setText(decimalFormat.format(newProductModel.getPrice()) +"");

            totalPrice = newProductModel.getPrice() * totalQuantity;
            productImg = newProductModel.getImg_url();
            productPrice = newProductModel.getPrice();
            productId = newProductModel.getId();
        }

        //Popular Products
        if (popularProductModel != null) {
            Glide.with(getApplicationContext()).load(popularProductModel.getImg_url()).into(detailImg);
            name.setText(popularProductModel.getName());
            ratingText.setText(popularProductModel.getRating());
            description.setText(popularProductModel.getDescription());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            price.setText(decimalFormat.format(popularProductModel.getPrice()) + "");

            totalPrice = popularProductModel.getPrice() * totalQuantity;
            productImg = popularProductModel.getImg_url();
            productPrice = popularProductModel.getPrice();
            productId = popularProductModel.getId();
        }

        //Show All
        if (showAllModel != null) {
            Glide.with(getApplicationContext()).load(showAllModel.getImg_url()).into(detailImg);
            name.setText(showAllModel.getName());
            ratingText.setText(showAllModel.getRating());
            description.setText(showAllModel.getDescription());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            price.setText(decimalFormat.format(showAllModel.getPrice()) + "");

            totalPrice = showAllModel.getPrice() * totalQuantity;
            productImg = showAllModel.getImg_url();
            productPrice = showAllModel.getPrice();
            productId = showAllModel.getId();
        }

        //View Cart
        cartImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailActivity.this, CartActivity.class));
            }
        });

        //Add to Cart
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity < 10) {
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));

                    if (newProductModel != null) {
                        totalPrice = newProductModel.getPrice() * totalQuantity;
                    }
                    if (popularProductModel != null) {
                        totalPrice = popularProductModel.getPrice() * totalQuantity;
                    }
                    if (showAllModel != null) {
                        totalPrice = showAllModel.getPrice() * totalQuantity;
                    }
                }
            }
        });

        removeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity > 1) {
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });

    }

    private void addToCart() {

        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                                if(myCartModel.getProductName().equals(name.getText().toString())){
                                    qtyEnd = myCartModel.getTotalQuantity();
                                    priceEnd = myCartModel.getTotalPrice();
                                    doc.getReference().delete();
                                }
                            }
                            //Add new data
                            final HashMap<String, Object> cartMap = new HashMap<>();
                            cartMap.put("productName", name.getText().toString());
                            cartMap.put("productImage", productImg);
                            cartMap.put("productPrice", productPrice);
                            cartMap.put("totalQuantity", Integer.parseInt(quantity.getText().toString()) + qtyEnd);
                            cartMap.put("totalPrice", totalPrice + priceEnd);
                            cartMap.put("currentTime", saveCurrentTime);
                            cartMap.put("currentDate", saveCurrentDate);
                            cartMap.put("productId", productId);

                            firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                    .collection("User").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            Toast.makeText(DetailActivity.this, "Successfully! Added To A Cart.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

    }
}