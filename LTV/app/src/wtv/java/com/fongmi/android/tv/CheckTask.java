package com.fongmi.android.tv;

import android.os.AsyncTask;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncCallback;

import java.net.HttpURLConnection;
import java.net.URL;

class CheckTask extends AsyncTask<Void, Integer, String> {

	private AsyncCallback callback;
	private Channel channel;

	static void execute(AsyncCallback callback, Channel channel) {
		new CheckTask(callback, channel).execute();
	}

	private CheckTask(AsyncCallback callback, Channel channel) {
		this.callback = callback;
		this.channel = channel;
	}

	@Override
	protected void onPreExecute() {
		if (channel.isToken()) channel.setUrl(channel.getUrl().concat(Token.get()));
	}

	@Override
	protected String doInBackground(Void... voids) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(channel.getUrl()).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			conn.getInputStream();
			boolean redirect = conn.getResponseCode() / 100 == 3;
			return redirect ? conn.getHeaderField("Location") : channel.getUrl();
		} catch (Exception e) {
			return channel.getUrl();
		}
	}

	@Override
	protected void onPostExecute(String result) {
		callback.onResponse(result);
	}
}