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

	private final String BASE = "http://127.0.0.1:6677/stream/";
	private final Handler handler;
	private AsyncCallback callback;
	private HttpURLConnection conn;
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
		if (conn != null) conn.disconnect();
	}

	public void destroy() {
		try {
			stop(); com.east.android.zlive.ZLive.INSTANCE.OnLiveStop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getLive(String uuid) {
		return BASE + "live?uuid=" + uuid;
	}

	private String getOpen(String uuid) {
		return BASE + "open?uuid=" + uuid;
	}

	private String getClose(String uuid) {
		return BASE + "close?uuid=" + uuid;
	}

	public void onPrepare(String source) {
		String[] split = source.split("/");
		String server = split[2];
		String uuid = split[3];
		if (current != null) connect(getClose(uuid));
		connect(getOpen(current = uuid));
		String param = "&group=5850&mac=00:00:00:00:00:00&dir=";
		String result = getLive(uuid) + "&server=" + server + param + FileUtil.getCachePath().getAbsolutePath();
		handler.post(() -> callback.onResponse(result));
	}

	private void connect(String url) {
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
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
