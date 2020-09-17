package com.forcetech.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.forcetech.android.ForceTV;

public class P6PService extends Service {

	private ForceTV forceTV;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		this.forceTV = new ForceTV();
		this.forceTV.start("p6p", 9910);
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (forceTV != null) forceTV.stop();
		return super.onUnbind(intent);
	}
}
