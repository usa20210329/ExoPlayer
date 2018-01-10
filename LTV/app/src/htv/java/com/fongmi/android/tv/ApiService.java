package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.fongmi.android.tv.utils.Utils;

public class ApiService extends BaseApiService {

    @Override
    public void getChannels(final AsyncTaskRunnerCallback callback) {
        Utils.getChannels(callback);
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncTaskRunnerCallback callback) {
        callback.onResponse(channel.getUrl());
    }

    @Override
    public void onRetry(AsyncTaskRunnerCallback callback) {
        callback.onResponse();
    }
}
