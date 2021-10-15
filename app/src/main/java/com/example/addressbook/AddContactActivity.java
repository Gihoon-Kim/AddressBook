package com.example.addressbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddContactActivity extends AppCompatActivity {

    private final int GALLERY_CODE = 10;

    @BindView(R.id.iv_profile)
    ImageView iv_profile;
    @BindView(R.id.et_firstName)
    EditText et_firstName;
    @BindView(R.id.et_lastName)
    EditText et_lastName;
    @BindView(R.id.et_Email)
    EditText et_Email;
    @BindView(R.id.et_PhoneNumber)
    EditText et_PhoneNumber;
    @BindView(R.id.btn_Save)
    Button btn_Save;
    @BindView(R.id.btn_Cancel)
    Button btn_Cancel;

    String uid;
    String imageCode;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef = database.getReference(); // Realtime Database
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontactactivity);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        Log.d("AddContactActivity UiD", uid);
    }

    @OnClick(R.id.iv_profile)
    public void onProfileClicked() {

        if (et_lastName.getText().toString().equals("")) {

            Toast.makeText(AddContactActivity.this, "Please set last name up first", Toast.LENGTH_SHORT).show();
        } else {

            loadAlbum();
        }
    }

    private void loadAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        Save Image into Database
         */
        if (requestCode == GALLERY_CODE) {

            Uri file = data.getData();
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("photo/" + et_lastName.getText().toString() + ".png");
            UploadTask uploadTask = riversRef.putFile(file);

            try {

                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                iv_profile.setImageBitmap(img);
            } catch (Exception e) {

                e.printStackTrace();
            }

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(AddContactActivity.this, "Update photo Failure", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(AddContactActivity.this, "Update Photo Successful", Toast.LENGTH_SHORT).show();
                }
            });

            Drawable image = iv_profile.getDrawable();
            String simage = "";
            Bitmap bitmap = ((BitmapDrawable)image).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] reviewImage = stream.toByteArray();
            imageCode = byteArrayToBinaryString(reviewImage);
        }
    }

    private String byteArrayToBinaryString(byte[] reviewImage) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < reviewImage.length; ++i) {

            sb.append(byteToBinaryString(reviewImage[i]));
        }

        return sb.toString();
    }

    private String byteToBinaryString(byte b) {

        StringBuilder sb = new StringBuilder("00000000");

        for (int bit = 0; bit < 8; bit++) {

            if (((b >> bit) & 1) > 0) {

                sb.setCharAt(7 - bit, '1');
            }
        }

        return sb.toString();
    }

    @OnClick(R.id.btn_Save)
    public void onSaveClicked() {

        String strFirstName = et_firstName.getText().toString();
        String strLastName = et_lastName.getText().toString();
        String strEmail = et_Email.getText().toString();
        String strPhoneNumber = et_PhoneNumber.getText().toString();

        // Firebase processing
        Contact contact = new Contact(imageCode, strFirstName, strLastName, strEmail, strPhoneNumber);

        mDatabaseRef.child("UserAccount").child(uid).child("Contacts").child(strLastName).setValue(contact);

        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();

        finish();
    }

    @OnClick(R.id.btn_Cancel)
    public void onCancelClicked() {

        finish();
    }
}