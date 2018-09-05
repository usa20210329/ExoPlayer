package com.fongmi.android.tv;

import android.os.AsyncTask;

import com.fongmi.android.tv.network.AsyncCallback;

import java.net.HttpURLConnection;
import java.net.URL;

class CheckTask extends AsyncTask<String, Integer, String> {

	private AsyncCallback callback;

	static void execute(AsyncCallback callback, String url) {
		new CheckTask(callback).execute(url);
	}

	private CheckTask(AsyncCallback callback) {
		this.callback = callback;
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(params[0]).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			conn.getInputStream();
			boolean redirect = conn.getResponseCode() / 100 == 3;
			return redirect ? conn.getHeaderField("Location") : params[0];
		} catch (Exception e) {
			return params[0];
		}
	}

	@Override
	protected void onPostExecute(String result) {
		callback.onResponse(result);
	}
}