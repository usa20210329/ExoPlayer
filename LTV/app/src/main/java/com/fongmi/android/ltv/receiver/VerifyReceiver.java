package com.fongmi.android.ltv.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.fongmi.android.ltv.App;
import com.fongmi.android.ltv.BuildConfig;


public class VerifyReceiver extends BroadcastReceiver {

	public static final String VERIFY = BuildConfig.APPLICATION_ID + ".action.VERIFY";

	private final Callback callback;
	private final Activity activity;

	public static VerifyReceiver create(Activity activity) {
		return new VerifyReceiver(activity);
	}

	public static void send() {
		App.get().sendBroadcast(new Intent(VERIFY));
	}

	public VerifyReceiver(Activity activity) {
		activity.registerReceiver(this, new IntentFilter(VERIFY));
		this.callback = (Callback) activity;
		this.activity = activity;
	}

	public void cancel() {
		activity.unregisterReceiver(this);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		callback.onVerified();
	}

	public interface Callback {
		void onVerified();
	}
}
