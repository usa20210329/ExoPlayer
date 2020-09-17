package com.fongmi.android.ltv.network.task;

import android.os.AsyncTask;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.network.HttpHelper;
import com.fongmi.android.ltv.utils.Token;

import java.net.HttpURLConnection;

public class CheckLinkTask extends AsyncTask<Channel, Integer, String> {

	private AsyncCallback callback;

	public CheckLinkTask(AsyncCallback callback, Channel item) {
		this.callback = callback;
		this.execute(item);
	}

	@Override
	protected String doInBackground(Channel... items) {
		try {
			String url = items[0].getUrl();
			if (items[0].isToken()) url = url.concat(Token.get());
			HttpURLConnection conn = HttpHelper.connect(url);
			if (HttpHelper.isRedirect(conn)) return conn.getHeaderField("Location");
			if (HttpHelper.isFile(conn)) return HttpHelper.download(conn);
			return url;
		} catch (Exception e) {
			return items[0].getUrl();
		}
	}

	@Override
	protected void onPostExecute(String url) {
		callback.onResponse(url);
	}
}