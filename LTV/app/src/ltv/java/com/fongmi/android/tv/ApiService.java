package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.network.BaseApiService;

import static com.fongmi.android.tv.Constant.LTV_CHANNEL;
import static com.fongmi.android.tv.Constant.LTV_CHANNEL_URL;
import static com.fongmi.android.tv.Constant.LTV_GEO;
import static com.fongmi.android.tv.Constant.LTV_NOTICE;

public class ApiService extends BaseApiService {

    @Override
    public void onInit(AsyncTaskRunnerCallback callback) {
        new WebService(LTV_NOTICE).executeOnExecutor(mExecutor);
        new WebService(LTV_GEO, callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannels(AsyncTaskRunnerCallback callback) {
        new WebService(LTV_CHANNEL, callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncTaskRunnerCallback callback) {
        new WebService(LTV_CHANNEL_URL, channel.getNumber(), callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void onRetry(AsyncTaskRunnerCallback callback) {
        new WebService(LTV_GEO, callback).executeOnExecutor(mExecutor);
    }
}
