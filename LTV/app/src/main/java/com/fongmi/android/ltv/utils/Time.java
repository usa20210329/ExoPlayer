package com.fongmi.android.ltv.utils;

import android.app.Activity;
import android.widget.TextView;

import com.fongmi.android.ltv.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Time {

	private Timer mTimer;

	private static class Loader {
		static volatile Time INSTANCE = new Time();
	}

	public static Time getInstance() {
		return Loader.INSTANCE;
	}

	public void set(Activity activity) {
		TextView view = activity.findViewById(R.id.time);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				activity.runOnUiThread(() -> view.setText(sdf.format(Calendar.getInstance().getTimeInMillis())));
			}
		}, 0, 1000);
	}

	public void cancel() {
		if (mTimer != null) mTimer.cancel();
	}
}
