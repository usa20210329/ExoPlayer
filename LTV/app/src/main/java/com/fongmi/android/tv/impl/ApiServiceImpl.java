package com.fongmi.android.tv.impl;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncCallback;

public interface ApiServiceImpl {

	void getList(AsyncCallback callback);

	void getUrl(Channel channel, AsyncCallback callback);
}
