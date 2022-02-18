package com.fongmi.android.tv.source;

import android.os.Handler;
import android.os.Looper;

import com.fongmi.android.tv.impl.AsyncCallback;
import com.fongmi.android.tv.utils.FileUtil;
import com.google.android.exoplayer2.PlaybackException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ZLive {

	private final Handler handler;
	private AsyncCallback callback;
	private String current;

	private static class Loader {
		static volatile ZLive INSTANCE = new ZLive();
	}

	public static ZLive get() {
		return Loader.INSTANCE;
	}

	public ZLive() {
		this.handler = new Handler(Looper.getMainLooper());
	}

	public void init() {
		com.east.android.zlive.ZLive.INSTANCE.OnLiveStart(6677L);
	}

	public void start(AsyncCallback callback, String source) {
		this.callback = callback;
		this.onPrepare(source);
	}

	public void stop() {
		if (current != null) new Thread(() -> connect("close", current)).start();
	}

	public void destroy() {
		try {
			com.east.android.zlive.ZLive.INSTANCE.OnLiveStop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onPrepare(String source) {
		String[] split = source.split("/");
		String server = split[2];
		String uuid = split[3];
		connect("open", current = uuid);
		String result = "http://127.0.0.1:6677/stream/live?uuid=" + uuid + "&server=" + server + "&group=5850&mac=00:00:00:00:00:00&dir=" + FileUtil.getCachePath().getAbsolutePath();
		handler.post(() -> callback.onResponse(result));
	}

	private void connect(String action, String uuid) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("http://127.0.0.1:6677/stream/" + action + "?uuid=" + uuid).openConnection();
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("accept", "*/*");
			conn.connect();
			conn.getInputStream();
		} catch (IOException e) {
			if (callback == null) return;
			handler.post(() -> callback.onError(new PlaybackException(null, null, 0)));
		}
	}
}
