package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.fongmi.android.tv.utils.Utils;

import static com.fongmi.android.library.ltv.Constant.LTV_CHANNEL_URL;
import static com.fongmi.android.library.ltv.Constant.LTV_SAMPLE;

public class ApiService extends BaseApiService {

	@Override
	public void onInit(AsyncCallback callback) {
		new WebService(LTV_SAMPLE, callback).executeOnExecutor(mExecutor);
	}

	@Override
	public void getChannels(AsyncCallback callback) {
		Utils.getChannels(callback);
	}

	@Override
	public void getChannelUrl(Channel channel, AsyncCallback callback) {
		new WebService(LTV_CHANNEL_URL, callback).executeOnExecutor(mExecutor, channel.getUrl());
	}

	@Override
	public void onRetry(AsyncCallback callback) {
		new WebService(LTV_SAMPLE, callback).executeOnExecutor(mExecutor);
	}
}
