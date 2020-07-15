package com.fongmi.android.ltv.network;

import com.fongmi.android.ltv.utils.HttpUtil;

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
			HttpURLConnection conn = HttpUtil.connect(url);
			if (HttpUtil.isFile(conn)) HttpUtil.download(conn);
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}