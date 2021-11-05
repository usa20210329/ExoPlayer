package com.fongmi.android.tv.network.task;

import android.os.Handler;
import android.os.Looper;

import com.fongmi.android.tv.utils.Token;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IPTask {

	private ExecutorService executor;
	private final Handler handler;

	public static IPTask create() {
		return new IPTask();
	}

	public IPTask() {
		this.executor = Executors.newSingleThreadExecutor();
		this.handler = new Handler(Looper.getMainLooper());
	}

	public void run() {
		executor.submit(this::doInBackground);
	}

	private void doInBackground() {
		try {
			Document doc = Jsoup.connect("https://www.whatismyip.com.tw/").get();
			onPostExecute(doc.select("span[data-ip]").text());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onPostExecute(String ip) {
		handler.postDelayed(() -> Token.updateUser(ip), 2000);
	}
}