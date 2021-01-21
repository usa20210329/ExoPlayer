package com.fongmi.android.ltv.network;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.network.task.DynamicTask;
import com.fongmi.android.ltv.network.task.EpgTask;
import com.google.firebase.database.FirebaseDatabase;

public class ApiService {

	private DynamicTask dTask;
	private EpgTask eTask;

	private static class Loader {
		static volatile ApiService INSTANCE = new ApiService();
	}

	private static ApiService get() {
		return Loader.INSTANCE;
	}

	public static void getConfig(AsyncCallback callback) {
		FirebaseDatabase.getInstance().getReference().addValueEventListener(callback);
	}

	public static void getUrl(Channel item, AsyncCallback callback) {
		if (get().dTask != null) get().dTask.cancel();
		if (item.isDynamic()) get().dTask = DynamicTask.create(callback).run(item);
		else callback.onResponse(item.getUrl());
	}

	public static void getEpg(Channel item, AsyncCallback callback) {
		if (get().eTask != null) get().eTask.cancel();
		get().eTask = EpgTask.create(callback).run(item);
	}
}
