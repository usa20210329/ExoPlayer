package com.fongmi.android.ltv.network.task;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.network.Connector;
import com.fongmi.android.ltv.source.TvBus;
import com.fongmi.android.ltv.utils.Token;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DynamicTask {

	private ExecutorService executor;
	private AsyncCallback callback;

	public DynamicTask(AsyncCallback callback) {
		this.executor = Executors.newSingleThreadExecutor();
		this.callback = callback;
	}

	public DynamicTask run(Channel item) {
		executor.submit(() -> doInBackground(item));
		return this;
	}

	private void doInBackground(Channel item) {
		try {
			String url = item.getUrl();
			if (item.isToken()) url = url.concat(Token.get());
			if (item.isTvBus()) TvBus.get().start(callback, item.getUrl());
			else onPostExecute(Connector.link(url).getPath());
		} catch (Exception e) {
			onPostExecute(item.getUrl());
		}
	}

	private void onPostExecute(String url) {
		if (callback != null) callback.onResponse(url);
	}

	public void cancel() {
		if (executor != null) executor.shutdownNow();
		executor = null;
		callback = null;
	}
}