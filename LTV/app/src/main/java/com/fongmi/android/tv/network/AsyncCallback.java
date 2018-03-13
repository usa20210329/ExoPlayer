package com.fongmi.android.tv.network;

import com.fongmi.android.tv.model.Channel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public abstract class AsyncCallback implements ValueEventListener {

    public void onResponse(String result) {
    }

    public void onResponse(List<Channel> items) {
    }

    public void onResponse(boolean success) {
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
}
