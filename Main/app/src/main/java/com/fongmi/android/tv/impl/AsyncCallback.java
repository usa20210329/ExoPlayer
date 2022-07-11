package com.fongmi.android.tv.impl;

import androidx.annotation.NonNull;

import com.fongmi.android.tv.bean.Config;
import com.google.android.exoplayer2.PlaybackException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public abstract class AsyncCallback implements ValueEventListener, Callback {

	public void onResponse(boolean success) {
	}

	public void onResponse(String result) {
	}

	public void onResponse(Config config) {
	}

	public void onError(PlaybackException error) {
	}

	@Override
	public void onResponse(Response response) throws IOException {
	}

	@Override
	public void onFailure(Request request, IOException e) {
	}

	@Override
	public void onDataChange(@NonNull DataSnapshot data) {
		onResponse(Config.get(data));
	}

	@Override
	public void onCancelled(@NonNull DatabaseError data) {
	}
}
