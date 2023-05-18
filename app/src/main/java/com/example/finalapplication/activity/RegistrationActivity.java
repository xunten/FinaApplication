package com.example.finalapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword, editTextPhone;
    private FirebaseAuth auth;
    FirebaseFirestore firestore;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

//        sharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
//
//        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);
//
//        if (isFirstTime) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("firstTime", false);
//            editor.commit();
//
//            Intent intent = new Intent(RegistrationActivity.this, OnBoardingActivity.class);
//            startActivity(intent);
//            finish();
//        }

    }

    public void signUp(View view) {

        String userName = editTextName.getText().toString();
        String userEmail = editTextEmail.getText().toString();
        String userPassword = editTextPassword.getText().toString();
        String userPhone = editTextPhone.getText().toString();
        String userConfirmPassword = editTextConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Enter Name!", Toast.LENGTH_SHORT).show();
            return;
        }

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

        if (TextUtils.isEmpty(userConfirmPassword)) {
            Toast.makeText(this, "Enter Confirm Password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userPhone)) {
            Toast.makeText(this, "Enter Phone!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (userPassword.equals(userConfirmPassword)){
            auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        //Add Username
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .build();
                        auth.getCurrentUser().updateProfile(profileUpdates);

                        //Add mobile phone
                        firestore = FirebaseFirestore.getInstance();
                        Map<String, String> map = new HashMap<>();
                        map.put("userPhone", userPhone);
                        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                                .collection("Phone").document("Mobile_phone").set(map);

                        //Set role: User
                        Map<String, String> map2 = new HashMap<>();
                        map2.put("role", "user");
                        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                                .collection("Role").document("role_type").set(map2);

                        //Send Verify account
                        if (auth.getCurrentUser() != null) {
                            auth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Successfully Sign Up. Please check your email to verify your account.", Toast.LENGTH_SHORT).show();
                                                auth.signOut();
                                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Password Incorrect!", Toast.LENGTH_SHORT).show();
        }
        
    }

    public void signIn(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }


}