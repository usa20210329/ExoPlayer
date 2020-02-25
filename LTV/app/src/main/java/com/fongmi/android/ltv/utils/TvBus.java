package com.fongmi.android.ltv.utils;

import android.app.Activity;
import android.content.Intent;

import com.tvbus.engine.TVCore;
import com.tvbus.engine.TVListener;
import com.tvbus.engine.TVService;

import org.json.JSONException;
import org.json.JSONObject;

public class TvBus implements TVListener {

	private static TVCore mTVCore;

	private void startTVBus(Activity activity) {
		mTVCore = TVCore.getInstance();
		mTVCore.setTVListener(this);
		activity.startService(new Intent(activity, TVService.class));
	}

	@Override
	public void onPrepared(String result) {
		parseCallbackInfo("onPrepared", result);
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

	private void parseCallbackInfo(String event, String result) {
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		switch (event) {
			case "onPrepared":
				
				break;
		}
	}

	private void startChannel(String address, String accessCode) {
		if (accessCode == null) {
			mTVCore.start(address);
		} else {
			mTVCore.start(address, accessCode);
		}
	}
}
