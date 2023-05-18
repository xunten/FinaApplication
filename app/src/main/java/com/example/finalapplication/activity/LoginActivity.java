package com.example.finalapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    EditText  editTextEmail, editTextPassword;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        if(auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    public void signin(View view) {

        String userEmail = editTextEmail.getText().toString();
        String userPassword = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter Email Address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Enter Password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPassword.length() < 6) {
            Toast.makeText(this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    final String[] role = {""};
                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                            .collection("Role").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                            role[0] = doc.getString("role");
                                        }
                                        if(role[0].equals("user")){
                                            if (auth.getCurrentUser().isEmailVerified()) {
                                                // Email đã được xác thực, cho phép truy cập vào các tính năng của ứng dụng
                                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                auth.getCurrentUser().sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "Một email xác thực đã được gửi tới địa chỉ email của bạn. Vui lòng kiểm tra hộp thư đến của bạn.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "This account is admin, cannot login to This App!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Email or Password is not correct!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Email or Password is not correct!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signup(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    public void forgetPass(View view) {
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

}