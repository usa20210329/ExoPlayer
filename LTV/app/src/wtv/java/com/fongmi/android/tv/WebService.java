package com.fongmi.android.tv;

import android.os.AsyncTask;

import com.fongmi.android.library.ltv.Ltv;
import com.fongmi.android.tv.network.AsyncCallback;

import static com.fongmi.android.library.ltv.Constant.LTV_SAMPLE;

class WebService extends AsyncTask<String, Integer, String> {

	private AsyncCallback callback;
	private String action;

	WebService(String action, AsyncCallback callback) {
		this.action = action;
		this.callback = callback;
	}

	@Override
	protected String doInBackground(String... params) {
		switch (action) {
			case LTV_SAMPLE:
				return Ltv.getInstance().getSample();
			default:
				return Ltv.getInstance().getUrl(params[0]);
		}
	}

	@Override
	protected void onPostExecute(String result) {
		switch (action) {
			case LTV_SAMPLE:
				callback.onResponse();
				break;
			default:
				callback.onResponse(result);
				break;
		}
	}
}