package com.fongmi.android.ltv.network;

import androidx.annotation.NonNull;

import com.fongmi.android.ltv.model.Channel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public abstract class AsyncCallback implements ValueEventListener {

	public void onResponse(String result) {
	}

	public void onResponse(List<Channel> items) {
	}

	@Override
	public void onDataChange(@NonNull DataSnapshot data) {
	}

	@Override
	public void onCancelled(@NonNull DatabaseError data) {
	}
}
