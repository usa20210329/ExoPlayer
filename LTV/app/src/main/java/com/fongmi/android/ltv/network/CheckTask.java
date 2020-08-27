package com.fongmi.android.ltv.network;

import android.os.AsyncTask;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.utils.HttpUtil;
import com.fongmi.android.ltv.utils.Token;

import java.net.HttpURLConnection;

public class CheckTask extends AsyncTask<Channel, Integer, String> {

	private AsyncCallback callback;

	CheckTask(AsyncCallback callback, Channel item) {
		this.callback = callback;
		this.execute(item);
	}

	@Override
	protected String doInBackground(Channel... items) {
		try {
			String url = items[0].getUrl();
			if (items[0].isToken()) url = url.concat(Token.get());
			HttpURLConnection conn = HttpUtil.connect(url);
			if (HttpUtil.isRedirect(conn)) return conn.getHeaderField("Location");
			if (HttpUtil.isFile(conn)) return HttpUtil.download(conn);
			return url;
		} catch (Exception e) {
			return items[0].getUrl();
		}
	}

	@Override
	protected void onPostExecute(String url) {
		if (!isCancelled()) callback.onResponse(url);
	}
}