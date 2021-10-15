package com.example.addressbook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Contact> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    String uid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        Log.d("Uid = ", uid);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();  // Firebase database Connection

        databaseReference = database.getReference("UserAccount/" + uid + "/Contacts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get data from Firebase Database
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Contact contact = snapshot.getValue(Contact.class);
                    arrayList.add(contact);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // if error
                Log.e("MainActivity", String.valueOf(error.toException()));
            }
        });

        adapter = new CustomAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((holder, view, position) -> {

            Contact contact = adapter.getItem(position);
            Intent updateIntent = new Intent(MainActivity.this, UpdateContact.class);
        });
    }

    @OnClick(R.id.btn_AddContact)
    public void onAddContactClicked() {

        Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        databaseReference = database.getReference("UserAccount/" + uid + "/Contacts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get data from Firebase Database
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Contact contact = snapshot.getValue(Contact.class);
                    arrayList.add(contact);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // if error
                Log.e("MainActivity", String.valueOf(error.toException()));
            }
        });
    }
}
