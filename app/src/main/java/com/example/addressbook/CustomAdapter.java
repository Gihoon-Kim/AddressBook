package com.example.addressbook;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Contact> arrayList = new ArrayList<Contact>();
    private Context context;

    OnContactItemClickListener listener;

    public CustomAdapter(ArrayList<Contact> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        /*
        Load Image into Database
         */
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("photo/" + holder.tv_LastName.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    if (dataSnapshot.getKey().equals("image")) {

                        String image = dataSnapshot.getValue().toString();

                        byte[] b = binaryStringToByteArray(image);
                        ByteArrayInputStream is = new ByteArrayInputStream(b);
                        Drawable reviewImage = Drawable.createFromStream(is, holder.tv_LastName.getText().toString());
                        holder.iv_profile.setImageDrawable(reviewImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.onBind(arrayList.get(position));
    }

    public static byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }

    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }

    public void setOnItemClickListener(OnContactItemClickListener listener) {

        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public Contact getItem(int position) {

        return arrayList.get(position);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_profile;
        TextView tv_FirstName;
        TextView tv_LastName;
        TextView tv_Email;
        TextView tv_PhoneNumber;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.iv_profile = itemView.findViewById(R.id.iv_profile);
            this.tv_FirstName = itemView.findViewById(R.id.tv_FirstName);
            this.tv_LastName = itemView.findViewById(R.id.tv_LastName);
            this.tv_Email = itemView.findViewById(R.id.tv_Email);
            this.tv_PhoneNumber = itemView.findViewById(R.id.tv_PhoneNumber);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    if (listener != null) {

                        listener.onItemClick(CustomViewHolder.this, v, pos);
                    }
                }
            });
        }

        void onBind(Contact contact) {

            tv_FirstName.setText(contact.getFirstName());
            tv_LastName.setText(contact.getLastName());
            tv_Email.setText(contact.getEmail());
            tv_PhoneNumber.setText(contact.getPhoneNumber());
        }
    }
}
