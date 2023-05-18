package com.example.finalapplication.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalapplication.R;
import com.example.finalapplication.activity.LoginActivity;
import com.example.finalapplication.activity.ShowAllActivity;
import com.example.finalapplication.adapter.BrandAdapter;
import com.example.finalapplication.adapter.NewProductAdapter;
import com.example.finalapplication.adapter.PopularProductAdapter;
import com.example.finalapplication.model.BrandModel;
import com.example.finalapplication.model.NewProductModel;
import com.example.finalapplication.model.PopularProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    ImageView imageLogout, userImgHome;

    TextView newProductShowAll, popularShowAll, userNameHome, orderNow;

    LinearLayout linearLayout;
    ProgressDialog progressDialog;
    RecyclerView brandRecyclerview, newProductRecyclerview, popularProductRecyclerview;
    //Search view
    EditText actionSearch;

    //Brand recyclerview
    BrandAdapter brandAdapter;
    List<BrandModel> brandModelList;

    //New Product Rc
    NewProductAdapter newProductAdapter;
    List<NewProductModel> newProductModelList;

    //Popular Product Rc
    PopularProductAdapter popularProductAdapter;
    List<PopularProductModel> popularProductModelList;

    //FireStore
    FirebaseAuth auth;

    FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(getActivity());
        brandRecyclerview = root.findViewById(R.id.recycleViewBrand);
        newProductRecyclerview = root.findViewById(R.id.recycleViewNewProduct);
        popularProductRecyclerview = root.findViewById(R.id.recycleViewProductPopular);

        actionSearch = root.findViewById(R.id.action_search);

        newProductShowAll = root.findViewById(R.id.new_product_see_all);
        popularShowAll = root.findViewById(R.id.popular_product_see_all);

        orderNow = root.findViewById(R.id.tv_Oder_Now);

        imageLogout = root.findViewById(R.id.image_logout);

        userNameHome = root.findViewById(R.id.textView6);
        userNameHome.setText("Hi " + auth.getCurrentUser().getDisplayName());
        userImgHome = root.findViewById(R.id.imageView5);
        if(auth.getCurrentUser().getPhotoUrl() != null){
            Glide.with(getContext()).load(auth.getCurrentUser().getPhotoUrl()).into(userImgHome);
        }

        orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                startActivity(intent);
            }
        });

        newProductShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                startActivity(intent);
            }
        });

        popularShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("popular",1);
                startActivity(intent);
            }
        });

        linearLayout = root.findViewById(R.id.home_layout);
        linearLayout.setVisibility(View.GONE);

        progressDialog.setTitle("Welcome To My Shoes Shop TxT App");
        progressDialog.setMessage("Please wait......");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Logout
        imageLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getRootView().getContext());
                dialog.setTitle("Notification");
                dialog.setMessage("Are you sure you signed out?");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        auth.signOut();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
        });
        //search
//        actionSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                filterList.clear();
//
//                if(editable.toString().isEmpty() ) {
//
//                    popularProductRecyclerview.setAdapter(new PopularProductAdapter(getContext(), popularProductModelList, this));
//                    popularProductAdapter.notifyDataSetChanged();
//                }
//                else {
//                    Filter(editable.toString());
//                }
//
//            }
//        });


        //brand
        brandRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        brandModelList = new ArrayList<>();
        brandAdapter = new BrandAdapter(getActivity(), brandModelList);
        brandRecyclerview.setAdapter(brandAdapter);

        db.collection("Brand")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BrandModel brandModel = document.toObject(BrandModel.class);
                                brandModelList.add(brandModel);
                                brandAdapter.notifyDataSetChanged();
                                linearLayout.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();

                            }
                        } else {
                            Toast.makeText(getActivity(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //New Products
        newProductRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        newProductModelList = new ArrayList<>();
        newProductAdapter = new NewProductAdapter(getContext(),newProductModelList);
        newProductRecyclerview.setAdapter(newProductAdapter);

        db.collection("NewProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                NewProductModel newProductModel = document.toObject(NewProductModel.class);
                                newProductModelList.add(newProductModel);
                                newProductAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //Popular Product
        popularProductRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        popularProductModelList = new ArrayList<>();
        popularProductAdapter = new PopularProductAdapter(getContext(),popularProductModelList);
        popularProductRecyclerview.setAdapter(popularProductAdapter);

        db.collection("AllProducts").limit(Long.parseLong("6"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                PopularProductModel popularProductModel = document.toObject(PopularProductModel.class);
                                popularProductModelList.add(popularProductModel);
                                popularProductAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        return root;
    }

//    private void Filter(String toString) {
//        for (PopularProductModel product:popularProductModelList) {
//            if (product.getName().equals(toString)) {
//                filterList.add(product);
//
//            }
//        }
//        popularProductRecyclerview.setAdapter(new PopularProductAdapter(getContext(), popularProductModelList, this));
//        popularProductAdapter.notifyDataSetChanged();
//    }
}