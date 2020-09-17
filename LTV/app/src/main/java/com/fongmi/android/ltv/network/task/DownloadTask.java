package com.fongmi.android.ltv.network.task;

import com.fongmi.android.ltv.network.HttpHelper;

import java.net.HttpURLConnection;
import java.util.TimerTask;

public class DownloadTask extends TimerTask {

	private String url;

	public DownloadTask(String url) {
		this.url = url;
	}

	@Override
	public void run() {
		try {
			HttpURLConnection conn = HttpHelper.connect(url);
			if (HttpHelper.isFile(conn)) HttpHelper.download(conn);
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}