package com.fongmi.android.tv.impl;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncCallback;

public interface ApiServiceImpl {

	void onInit(AsyncCallback callback);

	void getChannels(AsyncCallback callback);

	void getChannelUrl(Channel channel, AsyncCallback callback);

	void onRetry(AsyncCallback callback);
}
