package com.fongmi.android.ltv.network.task;

import android.os.AsyncTask;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.network.Connector;
import com.fongmi.android.ltv.utils.Token;

public class DynamicTask extends AsyncTask<Channel, Integer, String> {

	private final AsyncCallback callback;

	public DynamicTask(AsyncCallback callback, Channel item) {
		this.callback = callback;
		this.execute(item);
	}

	@Override
	protected String doInBackground(Channel... items) {
		try {
			String url = items[0].getUrl();
			if (items[0].isToken()) url = url.concat(Token.get());
			return Connector.link(url).getPath();
		} catch (Exception e) {
			return items[0].getUrl();
		}
	}

	@Override
	protected void onPostExecute(String url) {
		callback.onResponse(url);
	}
}