package com.example.addressbook;

import android.view.View;

public interface OnContactItemClickListener {

    public void onItemClick(CustomAdapter.CustomViewHolder holder, View view, int position);
}
