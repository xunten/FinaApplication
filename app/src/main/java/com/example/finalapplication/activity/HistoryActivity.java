package com.example.finalapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapplication.R;
import com.example.finalapplication.adapter.HistoryAdapter;
import com.example.finalapplication.model.HistoryModel;
import com.example.finalapplication.model.HistoryProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout progressbar;
    RecyclerView rc_history;
    Toolbar toolbarHistory;
    TextView tvHistoryEmpty;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    List<HistoryProductModel> productList;

    List<HistoryModel> list;
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initView();
        initToolbar();
        getOrder();
    }

    private void getOrder() {
        //Load data
        firestore.collection("Orders").orderBy("date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    //Thành công
                    for (DocumentSnapshot document : task.getResult()) {
                        //Get information
                        String id = document.getId();
                        String name = (String) document.get("name");
                        String phone = (String) document.get("phone");
                        String email = (String) document.get("email");
                        String address = (String) document.get("address");
                        String date = (String) document.get("date");
                        String status = (String) document.get("status");
                        int total = Integer.parseInt(document.get("total").toString());

                        if(auth.getCurrentUser().getDisplayName().equals(name)) {
                            //Get order detail
                            document.getReference().collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        productList = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            productList.add(document.toObject(HistoryProductModel.class));
                                        }
                                        // Tạo đối tượng OrderModel và thêm vào danh sách list
                                        HistoryModel historyModel = new HistoryModel(id, name, phone, email,
                                                address, date, total, status, productList);
                                        list.add(historyModel);

                                        // Gán danh sách list cho OrderAdapter và hiển thị lên RecyclerView
                                        historyAdapter = new HistoryAdapter(getApplicationContext(), list);
                                        rc_history.setAdapter(historyAdapter);
                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                    progressbar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException() + " :Lỗi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbarHistory);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarHistory.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        progressbar = findViewById(R.id.progressbar_his);
        rc_history = findViewById(R.id.rc_history);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rc_history.setLayoutManager(layoutManager);
        toolbarHistory = findViewById(R.id.toolbarHistory);
        tvHistoryEmpty = findViewById(R.id.tvHistoryEmpty);

        list = new ArrayList<>();
    }
}