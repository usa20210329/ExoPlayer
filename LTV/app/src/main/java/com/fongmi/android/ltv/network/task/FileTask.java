package com.fongmi.android.ltv.network.task;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.network.Connector;
import com.fongmi.android.ltv.utils.FileUtil;

import java.util.Timer;
import java.util.TimerTask;

public class FileTask {

	private Timer timer;

	private static class Loader {
		static volatile FileTask INSTANCE = new FileTask();
	}

	private static FileTask get() {
		return Loader.INSTANCE;
	}

	public static void start(Channel item, String url) {
		destroy(); if (FileUtil.isFile(url)) get().run(item.getUrl());
	}

	public static void destroy() {
		get().stop();
	}

	private void run(String url) {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				download(url);
			}
		}, 1000, 1000);
	}

	private void download(String url) {
		try {
			Connector.link(url).download();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stop() {
		if (timer != null) timer.cancel();
	}
}
