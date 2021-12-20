package com.fongmi.android.tv;

import android.app.Application;
import android.content.Context;

import com.tvbus.engine.PmsHook;

public class App extends Application {

	private static App instance;

	public App() {
		instance = this;
	}

	public static App get() {
		return instance;
	}

	@Override
	protected void attachBaseContext(Context base) {
		PmsHook.inject(base);
		super.attachBaseContext(base);
	}
}