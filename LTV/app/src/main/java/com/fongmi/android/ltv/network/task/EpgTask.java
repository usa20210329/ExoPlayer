package com.fongmi.android.ltv.network.task;

import android.os.Handler;
import android.os.Looper;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.network.Connector;
import com.fongmi.android.ltv.utils.Token;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EpgTask {

	private final ExecutorService executor;
	private final AsyncCallback callback;
	private final Handler handler;

	public static EpgTask create(AsyncCallback callback) {
		return new EpgTask(callback);
	}

	public EpgTask(AsyncCallback callback) {
		this.executor = Executors.newSingleThreadExecutor();
		this.handler = new Handler(Looper.getMainLooper());
		this.callback = callback;
	}

	public EpgTask run(Channel item) {
		executor.submit(() -> doInBackground(item));
		return this;
	}

	private void doInBackground(Channel item) {
		try {
			onPostExecute(Connector.link(Token.getEpg(item.getEpg())).getResult());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onPostExecute(String result) {
		handler.post(() -> callback.onResponse(result));
	}
}