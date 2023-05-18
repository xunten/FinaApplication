package com.example.finalapplication.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalapplication.R;
import com.example.finalapplication.activity.HistoryActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment {
    LinearLayout buttonSettings, btnHistory, btnLogout;
    TextView userEmail, userName, userMobile, userAddress, btnChangePassword;
    ImageView userImage;
    BottomSheetDialog dialog;
    CardView camera;

    String name, email;
    String str_hinhanh = "";

    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        userEmail = root.findViewById(R.id.userEmail);
        userName = root.findViewById(R.id.userName);
        userImage = root.findViewById(R.id.userImage);
        userMobile = root.findViewById(R.id.tvMobile);
        userAddress = root.findViewById(R.id.tvAddress);
        btnChangePassword = root.findViewById(R.id.tvPassword);

        //Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        //Set data into CardView Profile
        if (user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
        }
        if(user.getPhotoUrl() != null){
            Glide.with(getContext()).load(user.getPhotoUrl()).into(userImage);
        }
        final String[] mobile_phone = {""};
        String mobile;
        firestore.collection("CurrentUser").document(user.getUid())
                .collection("Phone").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                mobile_phone[0] = doc.getString("userPhone");
                            }
                            userMobile.setText(mobile_phone[0]);
                        }
                    }
                });
        firestore.collection("CurrentUser").document(user.getUid())
                .collection("Address").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int qtyAddress = 0;
                        if (task.isSuccessful()) {
                            qtyAddress = task.getResult().getDocuments().size();
                        }
                        userAddress.setText(qtyAddress + " Address");
                    }
                });

        //Set onClick History
        btnHistory = root.findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HistoryActivity.class));
            }
        });

        //Set onClick button Setting
        buttonSettings = root.findViewById(R.id.btnSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);
                View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_layout, null);

                TextView name = sheetView.findViewById(R.id.userNameEdit);
                TextView phone = sheetView.findViewById(R.id.userPhoneEdit);
//                TextView img = sheetView.findViewById(R.id.userImgUrlEdit);

                //Set data into form EditProfile
                name.setText(user.getDisplayName());
                phone.setText(userMobile.getText());
//                if(user.getPhotoUrl() != null ){
//                    img.setText(user.getPhotoUrl().toString());
//                }

                sheetView.findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String userName = name.getText().toString();
                        String userPhone = phone.getText().toString();
//                        String userImg = img.getText().toString();

                        //Update Name and Image URL User
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
//                                .setPhotoUri(Uri.parse(userImg))
                                .build();
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Edited your profile", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        //Update phone
                        DocumentReference phoneUpdate = firestore.collection("CurrentUser")
                                .document(user.getUid()).collection("Phone").document("Mobile_phone");
                        phoneUpdate.update("userPhone", userPhone);

                        dialog.dismiss();
                    }
                });
                dialog.setContentView(sheetView);
                dialog.show();
            }
        });

        //Edit avatar
        camera = root.findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProfileFragment.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        //Change password
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo và hiển thị hộp thoại
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_change_pasword, null);
                builder.setView(dialogView);

                // Lấy reference đến các EditText trong hộp thoại
                EditText oldPasswordEditText = dialogView.findViewById(R.id.oldPasswordEditText);
                EditText newPasswordEditText = dialogView.findViewById(R.id.newPasswordEditText);
                EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirmPasswordEditText);

                // Thiết lập các thuộc tính và sự kiện cho hộp thoại
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String oldPassword = oldPasswordEditText.getText().toString().trim();
                                String newPassword = newPasswordEditText.getText().toString().trim();
                                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                                // Kiểm tra các trường mật khẩu và thực hiện thay đổi mật khẩu
                                if (newPassword.equals(confirmPassword)) {
                                    // Xác thực người dùng hiện tại
                                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                                    user.reauthenticate(credential)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Thay đổi mật khẩu
                                                        user.updatePassword(newPassword)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(getContext(), "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            Toast.makeText(getContext(), "Thay đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(getContext(), "Xác thực mật khẩu cũ không thành công", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(getContext(), "Mật khẩu mới và nhập lại mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                // Tạo và hiển thị hộp thoại
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            Uri filePath = data.getData();

            // Hiển thị ảnh lên ImageView
            userImage.setImageURI(filePath);

            // Tạo một tài liệu mới trong Firestore
            DocumentReference newDocRef = firestore.collection("images").document();
            String newDocId = newDocRef.getId();

            // Lưu trữ ảnh vào Firebase Storage
            StorageReference imageRef = storageRef.child("images/" + newDocId + ".jpg");
            UploadTask uploadTask = imageRef.putFile(filePath);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {
                        // Lưu thông tin ảnh vào Firestore
                        str_hinhanh = downloadUri.toString();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(str_hinhanh))
                                .build();
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Edited your profile", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    // Lưu trữ thất bại
                }
            });
            Toast.makeText(getContext(), "Dang cap nhat hinh anh, vui long doi giay lat!", Toast.LENGTH_SHORT).show();
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}