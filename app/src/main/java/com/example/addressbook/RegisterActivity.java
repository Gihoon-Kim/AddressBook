package com.example.addressbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_ID)
    EditText et_ID;
    @BindView(R.id.et_Password)
    EditText et_Password;
    @BindView(R.id.btn_Register)
    Button btn_Register;

    private FirebaseAuth mFirebaseAuth; // Firebase Certification
    private DatabaseReference mDatabaseRef; // Realtime Database

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registactivity);

        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    }

    @OnClick(R.id.btn_Register)
    public void onRegister() {

        String strID = et_ID.getText().toString();
        String strPwd = et_Password.getText().toString();

        // Firebase Auth Processing
        mFirebaseAuth.createUserWithEmailAndPassword(strID, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                    UserAccount userAccount = new UserAccount();
                    userAccount.setIdToken(firebaseUser.getUid());
                    userAccount.setUserId(firebaseUser.getEmail());
                    userAccount.setUserPassword(strPwd);

                    // Insert UserAccount into database
                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(userAccount);

                    Toast.makeText(RegisterActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        finish();
    }

}
