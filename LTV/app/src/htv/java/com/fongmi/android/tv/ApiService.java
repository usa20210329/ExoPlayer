package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.fongmi.android.tv.utils.Utils;

public class ApiService extends BaseApiService {

    @Override
    public void getChannels(AsyncCallback callback) {
        Utils.getChannels(callback);
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncCallback callback) {
        callback.onResponse(channel.getUrl());
    }

    @Override
    public void onRetry(AsyncCallback callback) {
        callback.onResponse(false);
    }
}
