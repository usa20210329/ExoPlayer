package com.fongmi.android.ltv.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.fongmi.android.ltv.App;
import com.fongmi.android.ltv.BuildConfig;
import com.fongmi.android.ltv.impl.AsyncCallback;


public class VerifyReceiver extends BroadcastReceiver {

	public static final String VERIFY = BuildConfig.APPLICATION_ID + ".action.VERIFY";

	private AsyncCallback callback;

	public static VerifyReceiver create(AsyncCallback callback) {
		return new VerifyReceiver(callback);
	}

	public static void send() {
		App.get().sendBroadcast(new Intent(VERIFY));
	}

	public VerifyReceiver(AsyncCallback callback) {
		this.callback = callback;
	}

	public VerifyReceiver register(Activity activity) {
		activity.registerReceiver(this, new IntentFilter(VERIFY));
		return this;
	}

	public void cancel(Activity activity) {
		activity.unregisterReceiver(this);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		callback.onVerify();
	}
}
