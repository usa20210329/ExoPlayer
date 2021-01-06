package com.fongmi.android.ltv.network;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.bean.Config;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.network.task.DynamicTask;
import com.fongmi.android.ltv.network.task.EpgTask;
import com.fongmi.android.ltv.source.TvBus;
import com.fongmi.android.ltv.utils.FileUtil;
import com.fongmi.android.ltv.utils.Token;
import com.google.firebase.database.FirebaseDatabase;

public class ApiService {

	private DynamicTask task;

	private static class Loader {
		static volatile ApiService INSTANCE = new ApiService();
	}

	private static ApiService get() {
		return Loader.INSTANCE;
	}

	public static void getConfig(AsyncCallback callback) {
		FirebaseDatabase.getInstance().getReference().addValueEventListener(new AsyncCallback() {
			@Override
			public void onResponse(Config config) {
				Token.setConfig(config);
				callback.onResponse(config.getChannel());
				FileUtil.checkUpdate(config.getVersion());
			}
		});
	}

	public static void getUrl(Channel item, AsyncCallback callback) {
		TvBus.get().stop(); if (get().task != null) get().task.cancel();
		if (item.isDynamic()) get().task = DynamicTask.create(callback).run(item);
		else callback.onResponse(item.getUrl());
	}

	public static void getEpg(Channel item, AsyncCallback callback) {
		EpgTask.create(callback).run(item);
	}
}
