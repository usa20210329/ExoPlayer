package com.fongmi.android.tv.impl;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;

public interface ApiServiceImpl {

    void onInit(AsyncTaskRunnerCallback callback);

    void getChannels(AsyncTaskRunnerCallback callback);

    void getChannelUrl(Channel channel, AsyncTaskRunnerCallback callback);

    void onRetry(AsyncTaskRunnerCallback callback);
}
