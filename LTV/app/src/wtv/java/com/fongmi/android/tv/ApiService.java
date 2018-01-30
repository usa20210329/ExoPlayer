package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.fongmi.android.tv.utils.Utils;

import java.util.List;

import static com.fongmi.android.ltv.library.Constant.*;

public class ApiService extends BaseApiService {

    @Override
    public void onInit(AsyncTaskRunnerCallback callback) {
        new WebService(LTV_NOTICE).executeOnExecutor(mExecutor);
        new WebService(LTV_GEO, callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannels(AsyncTaskRunnerCallback callback) {
        Utils.getChannels(getCallback(callback));
    }

    private void getChannels(AsyncTaskRunnerCallback callback, List<Channel> extras) {
        new WebService(LTV_CHANNEL, getCallback(callback, extras)).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncTaskRunnerCallback callback) {
        new WebService(LTV_CHANNEL_URL, channel, callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void onRetry(AsyncTaskRunnerCallback callback) {
        new WebService(LTV_GEO, callback).executeOnExecutor(mExecutor);
    }

    private AsyncTaskRunnerCallback getCallback(final AsyncTaskRunnerCallback callback) {
        return new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse(List<Channel> items) {
                getChannels(callback, items);
            }
        };
    }

    private AsyncTaskRunnerCallback getCallback(final AsyncTaskRunnerCallback callback, final List<Channel> extras) {
        return new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse(List<Channel> items) {
                items.addAll(extras);
                callback.onResponse(items);
            }
        };
    }
}
