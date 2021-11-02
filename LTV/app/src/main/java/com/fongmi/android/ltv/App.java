package com.fongmi.android.ltv;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
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

	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseApp.initializeApp(this);
		FirebaseAppCheck.getInstance().installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance());
	}
}