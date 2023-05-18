package com.example.finalapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalapplication.R;
import com.example.finalapplication.adapter.ShowAllAdapter;
import com.example.finalapplication.model.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowAllProductActivity extends AppCompatActivity {
    LinearLayout progressbar;
    Toolbar toolbarShowall;
    RecyclerView recyclerView;
    ShowAllAdapter showAllAdapter;
    List<ShowAllModel> showAllModelList;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        progressbar = findViewById(R.id.progressbar_product);
        toolbarShowall = findViewById(R.id.toolbarShowall);
        setSupportActionBar(toolbarShowall);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarShowall.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.show_all_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        showAllModelList = new ArrayList<>();
        showAllAdapter = new ShowAllAdapter(this,showAllModelList);
        recyclerView.setAdapter(showAllAdapter);

        firestore.collection("AllProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            toolbarShowall.setTitle("All Products");
                            for (DocumentSnapshot doc :task.getResult().getDocuments()) {
                                ShowAllModel showAllModel = doc.toObject(ShowAllModel.class);
                                showAllModelList.add(showAllModel);
                                showAllAdapter.notifyDataSetChanged();
                            }
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
