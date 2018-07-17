package com.fongmi.android.tv;

import android.os.AsyncTask;

import com.fongmi.android.library.ltv.Ltv;
import com.fongmi.android.tv.network.AsyncCallback;

class WebService extends AsyncTask<String, Integer, String> {

	private AsyncCallback callback;

	WebService(AsyncCallback callback) {
		this.callback = callback;
	}

	@Override
	protected String doInBackground(String... params) {
		return Ltv.getInstance().getUrl(params[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		callback.onResponse(result);
	}
}