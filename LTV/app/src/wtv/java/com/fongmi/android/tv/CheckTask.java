package com.fongmi.android.tv;

import android.os.AsyncTask;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncCallback;

import java.net.HttpURLConnection;
import java.net.URL;

class CheckTask extends AsyncTask<Channel, Integer, String> {

	private AsyncCallback callback;

	static void execute(AsyncCallback callback, Channel item) {
		new CheckTask(callback).execute(item);
	}

	private CheckTask(AsyncCallback callback) {
		this.callback = callback;
	}

	@Override
	protected String doInBackground(Channel... channels) {
		try {
			String url = channels[0].getUrl();
			if (channels[0].isToken()) url = url.concat(Token.get());
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.connect();
			conn.getInputStream();
			return conn.getResponseCode() / 100 == 3 ? conn.getHeaderField("Location") : url;
		} catch (Exception e) {
			return channels[0].getUrl();
		}
	}

	@Override
	protected void onPostExecute(String result) {
		callback.onResponse(result);
	}
}