package com.fongmi.android.tv.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.BuildConfig;


public class VerifyReceiver extends BroadcastReceiver {

	public static final String VERIFY = BuildConfig.APPLICATION_ID + ".action.VERIFY";

	private final Callback callback;
	private final Activity activity;

	public static void create(Activity activity) {
		new VerifyReceiver(activity).register();
	}

	public static void send() {
		App.get().sendBroadcast(new Intent(VERIFY));
	}

	public VerifyReceiver(Activity activity) {
		this.callback = (Callback) activity;
		this.activity = activity;
	}

	private void register() {
		activity.registerReceiver(this, new IntentFilter(VERIFY));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		activity.unregisterReceiver(this);
		callback.onVerified();
	}

	public interface Callback {
		void onVerified();
	}
}
