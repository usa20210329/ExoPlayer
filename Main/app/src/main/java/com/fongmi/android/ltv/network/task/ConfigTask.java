package com.fongmi.android.ltv.network.task;

import android.os.Handler;
import android.os.Looper;

import com.fongmi.android.ltv.bean.Config;
import com.fongmi.android.ltv.impl.AsyncCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfigTask {

	private final ExecutorService executor;
	private AsyncCallback callback;
	private final Handler handler;

	public static ConfigTask create() {
		return new ConfigTask();
	}

	public ConfigTask() {
		this.executor = Executors.newSingleThreadExecutor();
		this.handler = new Handler(Looper.getMainLooper());
	}

	public void run(AsyncCallback callback) {
		this.callback = callback;
		this.executor.submit(this::doInBackground);
	}

	private void doInBackground() {
		try {
			Document doc = Jsoup.connect("https://github.com/FongMi/ExoPlayer/raw/master/Misc/config.json").get();
			onPostExecute(doc.text());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onPostExecute(String result) {
		handler.post(() -> callback.onResponse(Config.objectFrom(result)));
	}
}