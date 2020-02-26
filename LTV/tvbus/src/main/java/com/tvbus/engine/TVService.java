package com.tvbus.engine;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TVService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		TVServer server = new TVServer();
		Thread thread = new Thread(server);
		thread.setName("tvcore");
		thread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		TVCore.get().quit();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class TVServer implements Runnable {

		@Override
		public void run() {
			int ret = TVCore.get().init(getApplicationContext());
			if (ret == 0) TVCore.get().run();
		}
	}
}
