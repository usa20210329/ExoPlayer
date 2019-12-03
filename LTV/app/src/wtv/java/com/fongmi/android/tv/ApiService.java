package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.fongmi.android.tv.utils.Utils;

public class ApiService extends BaseApiService {

	@Override
	public void getList(AsyncCallback callback) {
		Utils.getList(callback);
		Token.setKey();
	}

	@Override
	public void getUrl(Channel channel, AsyncCallback callback) {
		CheckTask.execute(callback, channel);
	}
}
