package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.fongmi.android.tv.utils.Utils;

public class ApiService extends BaseApiService {

    @Override
    public void onInit(AsyncTaskRunnerCallback callback) {
        JsTask.execute(callback);
    }

    @Override
    public void getChannels(AsyncTaskRunnerCallback callback) {
        Utils.getChannels(callback);
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncTaskRunnerCallback callback) {
        TokenTask.execute(channel, callback);
    }

    @Override
    public void onRetry(AsyncTaskRunnerCallback callback) {
        JsTask.execute(callback);
    }
}
