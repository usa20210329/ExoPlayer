package com.fongmi.android.tv;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.fongmi.android.library.ltv.Ltv;
import com.fongmi.android.tv.network.AsyncCallback;
import com.fongmi.android.tv.utils.Notify;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import static com.fongmi.android.library.ltv.Constant.LTV_CHANNEL_URL;
import static com.fongmi.android.library.ltv.Constant.LTV_NOTICE;
import static com.fongmi.android.library.ltv.Constant.LTV_SAMPLE;

class WebService extends AsyncTask<Void, Integer, String> {

	private AsyncCallback callback;
	private String action;
	private String url;

	WebService(String action) {
		this.action = action;
	}

	WebService(String action, AsyncCallback callback) {
		this.action = action;
		this.callback = callback;
	}

	WebService(String action, String url, AsyncCallback callback) {
		this.url = url;
		this.action = action;
		this.callback = callback;
	}

	@Override
	protected String doInBackground(Void... params) {
		switch (action) {
			case LTV_NOTICE:
				return Ltv.getInstance().getNotice();
			case LTV_SAMPLE:
				return Ltv.getInstance().getSample();
			case LTV_CHANNEL_URL:
				return Ltv.getInstance().getUrl(url);
			default:
				return Ltv.getInstance().getGeo();
		}
	}

	@Override
	protected void onPostExecute(String result) {
		switch (action) {
			case LTV_NOTICE:
				Notify.alert(result);
				break;
			case LTV_SAMPLE:
				callback.onResponse();
				break;
			case LTV_CHANNEL_URL:
				callback.onResponse(result);
				break;
			default:
				getKey();
				break;
		}
	}

	private void getKey() {
		FirebaseDatabase.getInstance().getReference().child("key").addValueEventListener(new AsyncCallback() {
			@Override
			public void onDataChange(@NonNull DataSnapshot data) {
				Ltv.getInstance().setKey(data.getValue().toString());
			}
		});
	}
}