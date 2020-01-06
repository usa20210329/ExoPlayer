package com.fongmi.android.tv.network;

import androidx.annotation.NonNull;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.utils.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ApiService {

	public static void getList(AsyncCallback callback) {
		FirebaseDatabase.getInstance().getReference().child("channel").addValueEventListener(new AsyncCallback() {
			@Override
			public void onDataChange(@NonNull DataSnapshot data) {
				List<Channel> items = new ArrayList<>();
				for (DataSnapshot item : data.getChildren()) items.add(Channel.create(item));
				callback.onResponse(items);
				Token.setKey();
			}
		});
	}

	public static void getUrl(Channel item, AsyncCallback callback) {
		if (item.isToken()) {
			Task.execute(item, callback);
		} else {
			callback.onResponse(item.getUrl());
		}
	}
}
