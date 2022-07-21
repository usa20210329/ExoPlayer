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

public class Force {

	private final OkHttpClient client;
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
		this.client = new OkHttpClient();
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
		String id = uri.getLastPathSegment();
		String cmd = "http://127.0.0.1:" + ForceTV.getPort(source) + "/cmd.xml?cmd=switch_chan&server=" + uri.getHost() + ":" + uri.getPort() + "&id=" + id;
		String result = "http://127.0.0.1:" + ForceTV.getPort(source) + "/" + id;
		handler.post(() -> onResponse(result));
		connect(cmd);
	}

	private void connect(String url) {
		try {
			client.newCall(new Request.Builder().url(url).build()).execute();
		} catch (Exception e) {
			handler.post(this::onError);
		}
	}

	private void onResponse(String result) {
		if (callback != null) callback.onResponse(result);
	}

	private void onError() {
		if (callback != null) callback.onError(new PlaybackException(null, null, 0));
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
