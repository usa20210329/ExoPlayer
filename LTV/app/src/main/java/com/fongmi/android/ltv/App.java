package com.fongmi.android.ltv;

import android.app.Application;
import android.content.Context;

import com.fongmi.android.ltv.utils.Utils;
import com.tvbus.engine.PmsHook;

public class App extends Application {

	private static App instance;

	public App() {
		instance = this;
	}

	public static App get() {
		return instance;
	}

	public static String getName() {
		return Utils.getString(R.string.app_id).toLowerCase();
	}

	@Override
	protected void attachBaseContext(Context base) {
		PmsHook.inject(base);
		super.attachBaseContext(base);
	}
}