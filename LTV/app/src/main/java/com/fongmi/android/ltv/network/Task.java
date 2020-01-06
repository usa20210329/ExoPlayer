package com.fongmi.android.ltv.network;

import android.os.AsyncTask;

import com.fongmi.android.ltv.model.Channel;
import com.fongmi.android.ltv.utils.Token;

import java.net.HttpURLConnection;
import java.net.URL;

public class Task extends AsyncTask<Channel, Integer, String> {

	private AsyncCallback callback;

	Task(AsyncCallback callback) {
		this.callback = callback;
	}

	@Override
	protected String doInBackground(Channel... items) {
		try {
			String url = items[0].getUrl().concat(Token.get());
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.connect();
			conn.getInputStream();
			return conn.getResponseCode() / 100 == 3 ? conn.getHeaderField("Location") : url;
		} catch (Exception e) {
			return items[0].getUrl();
		}
	}

	@Override
	protected void onPostExecute(String result) {
		callback.onResponse(result);
	}
}