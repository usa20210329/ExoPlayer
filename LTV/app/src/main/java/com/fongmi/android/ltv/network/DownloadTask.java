package com.fongmi.android.ltv.network;

import com.fongmi.android.ltv.utils.HttpUtil;

import java.util.TimerTask;

public class DownloadTask extends TimerTask {

	private String url;

	public DownloadTask(String url) {
		this.url = url;
	}

	@Override
	public void run() {
		try {
			HttpUtil.download(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}