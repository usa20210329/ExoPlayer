package com.fongmi.android.ltv.utils;

import android.content.Intent;

import com.fongmi.android.ltv.App;
import com.fongmi.android.ltv.network.AsyncCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tvbus.engine.TVCore;
import com.tvbus.engine.TVListener;
import com.tvbus.engine.TVService;

public class TvBus implements TVListener {

	private AsyncCallback callback;
	private String url;

	private static class Loader {
		static volatile TvBus INSTANCE = new TvBus();
	}

	public static TvBus get() {
		return Loader.INSTANCE;
	}

	public void init() {
		TVCore.get().setTVListener(this);
		App.get().startService(new Intent(App.get(), TVService.class));
	}

	public void start(AsyncCallback callback, String url) {
		TVCore.get().start(url);
		setCallback(callback);
		setUrl(url);
	}

	private void setCallback(AsyncCallback callback) {
		this.callback = callback;
	}

	private void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void onPrepared(String result) {
		JsonObject json = new Gson().fromJson(result, JsonObject.class);
		if (json.get("hls") != null) callback.onResponse(json.get("hls").getAsString());
	}

	@Override
	public void onInited(String result) {
	}

	@Override
	public void onStart(String result) {
	}

	@Override
	public void onInfo(String result) {
	}

	@Override
	public void onStop(String result) {
		callback.onResponse(url);
	}

	@Override
	public void onQuit(String result) {
	}
}
