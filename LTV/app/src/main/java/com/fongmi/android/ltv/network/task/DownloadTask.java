package com.fongmi.android.ltv.network.task;

import com.fongmi.android.ltv.network.Connector;

import java.util.TimerTask;

public class DownloadTask extends TimerTask {

	private final String url;

	public DownloadTask(String url) {
		this.url = url;
	}

	@Override
	public void run() {
		try {
			Connector.link(url).download();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
