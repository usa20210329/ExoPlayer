package com.fongmi.android.ltv.source;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.fongmi.android.ltv.App;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.forcetech.service.P5PService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Force {

	private HttpURLConnection conn;
	private final Handler handler;

	private static class Loader {
		static volatile Force INSTANCE = new Force();
	}

	public static Force get() {
		return Loader.INSTANCE;
	}

	public Force() {
		this.handler = new Handler(Looper.getMainLooper());
	}

	public void init() {
		App.get().bindService(new Intent(App.get(), P5PService.class), connection, Context.BIND_AUTO_CREATE);
	}

	public static void destroy() {
		App.get().unbindService(get().connection);
	}

	private final ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};

	public void start(AsyncCallback callback, String source) {
		Uri uri = Uri.parse(source);
		String cmd = "http://127.0.0.1:6001/cmd.xml?cmd=switch_chan&server=" + uri.getHost() + ":" + uri.getPort() + "&id=";
		String tmp = uri.getLastPathSegment();
		int index = tmp.lastIndexOf(".");
		if (index == -1) cmd = cmd + tmp;
		else cmd = cmd + tmp.substring(0, index);
		connect(cmd + "&" + uri.getQuery());
		String result = "http://127.0.0.1:6001" + uri.getPath();
		handler.post(() -> callback.onResponse(result));
	}

	public void stop() {
		if (conn != null) conn.disconnect();
	}

	private void connect(String url) {
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "MTV");
			conn.setRequestProperty("accept", "*/*");
			conn.connect();
			new InputStreamReader(conn.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
