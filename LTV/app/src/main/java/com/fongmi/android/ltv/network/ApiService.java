package com.fongmi.android.ltv.network;

import androidx.annotation.NonNull;

import com.fongmi.android.ltv.bean.Channel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ApiService {

	private Task mTask;

	private static class Loader {
		static volatile ApiService INSTANCE = new ApiService();
	}

	public static ApiService getInstance() {
		return Loader.INSTANCE;
	}

	public void getList(AsyncCallback callback) {
		FirebaseDatabase.getInstance().getReference().child("channel").addValueEventListener(new AsyncCallback() {
			@Override
			public void onDataChange(@NonNull DataSnapshot data) {
				List<Channel> items = new ArrayList<>();
				for (DataSnapshot item : data.getChildren()) items.add(Channel.create(item));
				callback.onResponse(items);
			}
		});
	}

	public void getUrl(Channel item, AsyncCallback callback) {
		if (mTask != null) mTask.cancel(true);
		mTask = new Task(callback);
		mTask.execute(item);
	}
}
