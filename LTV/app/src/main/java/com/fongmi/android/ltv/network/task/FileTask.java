package com.fongmi.android.ltv.network.task;

import com.fongmi.android.ltv.network.Connector;

import java.util.Timer;
import java.util.TimerTask;

public class FileTask {

	private Timer timer;

	private static class Loader {
		static volatile FileTask INSTANCE = new FileTask();
	}

	private static FileTask getInstance() {
		return Loader.INSTANCE;
	}

	public static void start(String url) {
		getInstance().cancel();
		getInstance().run(url);
	}

	public static void destroy() {
		getInstance().cancel();
	}

	private void run(String url) {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				download(url);
			}
		}, 0, 1000);
	}

	private void download(String url) {
		try {
			Connector.link(url).download();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cancel() {
		if (timer != null) timer.cancel();
	}
}
