package com.fongmi.android.ltv.utils;

import android.content.Intent;

import com.fongmi.android.ltv.App;
import com.fongmi.android.ltv.impl.TvBusCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tvbus.engine.TVCore;
import com.tvbus.engine.TVListener;
import com.tvbus.engine.TVService;

public class TvBus implements TVListener {

	private TvBusCallback callback;

	private static class Loader {
		static volatile TvBus INSTANCE = new TvBus();
	}

	public static TvBus get() {
		return Loader.INSTANCE;
	}

	public void init(TvBusCallback callback) {
		this.callback = callback;
		TVCore.get().setTVListener(this);
		App.get().startService(new Intent(App.get(), TVService.class));
	}

	public void start(String url) {
		TVCore.get().start(url);
	}

	@Override
	public void onPrepared(String result) {
		JsonObject json = new Gson().fromJson(result, JsonObject.class);
		if (json.get("hls") != null) callback.onReady(json.get("hls").getAsString());
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
	}

	@Override
	public void onQuit(String result) {
	}
}
