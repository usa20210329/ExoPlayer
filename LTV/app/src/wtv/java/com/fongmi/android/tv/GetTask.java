package com.fongmi.android.tv;

import android.os.AsyncTask;

import com.fongmi.android.tv.network.AsyncCallback;

import java.net.HttpURLConnection;
import java.net.URL;

class GetTask extends AsyncTask<String, Void, String> {

	private AsyncCallback callback;

	GetTask(AsyncCallback callback) {
		this.callback = callback;
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			return getUrl(params[0]);
		} catch (Exception e) {
			return params[0];
		}
	}

	private String getUrl(String url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.connect();
		conn.getInputStream();
		boolean redirect = conn.getResponseCode() / 100 == 3;
		return redirect ? conn.getHeaderField("Location") : url;
	}

	@Override
	protected void onPostExecute(String result) {
		callback.onResponse(result);
	}
}
