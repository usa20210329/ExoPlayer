package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.model.Geo;
import com.fongmi.android.tv.network.AsyncTaskRunner;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.network.BaseApiService;

import static com.fongmi.android.tv.Constant.FREE_GEO;
import static com.fongmi.android.tv.Constant.WTV_CHANNEL;
import static com.fongmi.android.tv.Constant.WTV_NOTICE;

public class ApiService extends BaseApiService {

    @Override
    public void onInit(AsyncTaskRunnerCallback callback) {
        new AsyncTaskRunner(FREE_GEO, getCallback(callback)).executeOnExecutor(mExecutor);
        new WebService(UserData.getInstance().getSoap(WTV_NOTICE)).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannels(AsyncTaskRunnerCallback callback) {
        new WebService(UserData.getInstance().getSoap(WTV_CHANNEL), callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncTaskRunnerCallback callback) {
        callback.onResponse(channel.getUrl().replace("%20", ""));
    }

    @Override
    public void onRetry(AsyncTaskRunnerCallback callback) {
        new AsyncTaskRunner(FREE_GEO, getCallback(callback)).executeOnExecutor(mExecutor);
    }

    private AsyncTaskRunnerCallback getCallback(final AsyncTaskRunnerCallback callback) {
        return new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse(String result) {
                Geo.save(result);
                callback.onResponse();
            }
        };
    }
}
