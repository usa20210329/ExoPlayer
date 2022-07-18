package com.fongmi.android.tv.source;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.impl.AsyncCallback;
import com.forcetech.android.ForceTV;
import com.forcetech.service.P2PService;
import com.forcetech.service.P5PService;
import com.google.android.exoplayer2.PlaybackException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class Force {

	private final Handler handler;
	private AsyncCallback callback;

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
		App.get().bindService(new Intent(App.get(), P2PService.class), mConn, Context.BIND_AUTO_CREATE);
		App.get().bindService(new Intent(App.get(), P5PService.class), mConn, Context.BIND_AUTO_CREATE);
	}

	public void start(AsyncCallback callback, String source) {
		this.callback = callback;
		this.onPrepare(source);
	}

	public void destroy() {
		try {
			App.get().unbindService(mConn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onPrepare(String source) {
		Uri uri = Uri.parse(source);
		String cmd = "http://127.0.0.1:" + ForceTV.getPort(source) + "/cmd.xml?cmd=switch_chan&server=" + uri.getHost() + ":" + uri.getPort() + "&id=";
		String tmp = uri.getLastPathSegment();
		int index = tmp.lastIndexOf(".");
		if (index == -1) cmd = cmd + tmp;
		else cmd = cmd + tmp.substring(0, index);
		String result = "http://127.0.0.1:" + ForceTV.getPort(source) + uri.getPath();
		connect(cmd, result);
	}

	private void connect(String cmd, String result) {
		try {
			if (callback == null) return;
			Response response = new OkHttpClient().newCall(new Request.Builder().url(cmd).build()).execute();
			if (response.isSuccessful()) handler.post(() -> callback.onResponse(result));
			else handler.post(() -> callback.onError(new PlaybackException(null, null, 0)));
		} catch (Exception e) {
			handler.post(() -> callback.onError(new PlaybackException(null, null, 0)));
		}
	}

	private final ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};
}
