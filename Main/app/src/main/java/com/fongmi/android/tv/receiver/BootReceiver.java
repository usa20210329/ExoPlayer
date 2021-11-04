package com.fongmi.android.tv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fongmi.android.tv.utils.Prefers;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Prefers.isBoot()) startBoot(context);
	}

	private void startBoot(Context context) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
