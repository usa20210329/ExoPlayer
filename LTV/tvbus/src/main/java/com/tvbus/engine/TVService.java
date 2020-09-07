package com.tvbus.engine;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TVService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		Thread thread = new Thread(new TVServer());
		thread.setName("tvcore");
		thread.start();
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
			if (tvcore.init(getApplicationContext()) == 0) tvcore.run();
		}
	}
}
