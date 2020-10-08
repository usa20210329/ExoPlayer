package com.tvbus.engine;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class TVService extends Service {

	public static void start(Context context) {
		try {
			context.startService(new Intent(context, TVService.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void stop(Context context) {
		try {
			context.stopService(new Intent(context, TVService.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate() {
		new Thread(new TVServer()).start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		TVCore.getInstance().quit();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class TVServer implements Runnable {

		private TVCore tvcore;

		private TVServer() {
			this.tvcore = TVCore.getInstance();
		}

		@Override
		public void run() {
			tvcore.setPlayPort(8902);
			tvcore.setServPort(4010);
			int ret = tvcore.init(getApplicationContext());
			if (ret == 0) tvcore.run();
		}
	}
}
