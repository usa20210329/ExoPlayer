package com.fongmi.android.ltv.network;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.bean.Config;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.network.task.CheckLinkTask;
import com.fongmi.android.ltv.source.Force;
import com.fongmi.android.ltv.source.TvBus;
import com.fongmi.android.ltv.utils.FileUtil;
import com.fongmi.android.ltv.utils.Token;
import com.google.firebase.database.FirebaseDatabase;

public class ApiService {

	private CheckLinkTask mTask;

	private static class Loader {
		static volatile ApiService INSTANCE = new ApiService();
	}

	public static ApiService getInstance() {
		return Loader.INSTANCE;
	}

	public void getConfig(AsyncCallback callback) {
		FirebaseDatabase.getInstance().getReference().addValueEventListener(new AsyncCallback() {
			@Override
			public void onResponse(Config config) {
				setConfig(callback, config);
			}
		});
	}

	private void setConfig(AsyncCallback callback, Config config) {
		FileUtil.checkUpdate(config.getVersion());
		callback.onResponse(config.getChannel());
		Token.setConfig(config);
	}

	public void getUrl(Channel item, AsyncCallback callback) {
		TvBus.get().stop(); if (mTask != null) mTask.cancel(true);
		if (item.isTvBus()) TvBus.get().start(callback, item.getUrl());
		else if (item.isP2P()) Force.get().start(callback, item.getUrl());
		else if (item.isDynamic()) mTask = new CheckLinkTask(callback, item);
		else callback.onResponse(item.getUrl());
	}
}
