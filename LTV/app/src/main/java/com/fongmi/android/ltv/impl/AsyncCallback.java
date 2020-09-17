package com.fongmi.android.ltv.impl;

import androidx.annotation.NonNull;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.bean.Config;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public abstract class AsyncCallback implements ValueEventListener {

	public void onResponse(String url) {
	}

	public void onResponse(List<Channel> items) {
	}

	public void onResponse(Config config) {
	}

	@Override
	public void onDataChange(@NonNull DataSnapshot data) {
		onResponse(data.getValue(Config.class));
	}

	@Override
	public void onCancelled(@NonNull DatabaseError data) {
	}
}
