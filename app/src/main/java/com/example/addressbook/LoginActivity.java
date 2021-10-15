package com.example.addressbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_ID)
    EditText et_ID;
    @BindView(R.id.et_Password)
    EditText et_Password;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // Firebase Certification
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(); // Realtime Database

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_Register)
    public void onRegisterClicked() {

        // Move to register
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_Login)
    public void onLoginClicked() {

        String strID = et_ID.getText().toString();
        String strPwd = et_Password.getText().toString();

        mFirebaseAuth.signInWithEmailAndPassword(strID, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    // Login Successful
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    String uid = mFirebaseAuth.getUid();
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
