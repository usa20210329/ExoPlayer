package com.fongmi.android.ltv.network.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.impl.AsyncCallback;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DynamicTask extends AsyncTask<Channel, Integer, String> {

	private AsyncCallback callback;

	public DynamicTask(AsyncCallback callback, Channel item) {
		this.callback = callback;
		this.execute(item);
	}

	@Override
	protected String doInBackground(Channel... items) {
		try {
			String url = items[0].getUrl();
			HttpURLConnection conn = connect(url);
			String location = conn.getHeaderField("Location");
			return TextUtils.isEmpty(location) ? url : location;
		} catch (Exception e) {
			return items[0].getUrl();
		}
	}

	private HttpURLConnection connect(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		conn.setUseCaches(false);
		conn.setDoInput(false);
		conn.setDoInput(true);
		conn.connect();
		return conn;
	}

	@Override
	protected void onPostExecute(String url) {
		callback.onResponse(url);
	}
}