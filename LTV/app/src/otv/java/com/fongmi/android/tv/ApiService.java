package com.fongmi.android.tv;

import com.fongmi.android.library.otv.Otv;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.network.BaseApiService;

import static com.fongmi.android.library.otv.Constant.*;

public class ApiService extends BaseApiService {

    @Override
    public void onInit(AsyncTaskRunnerCallback callback) {
        new DataTask(OTV_INFO, callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannels(AsyncTaskRunnerCallback callback) {
        new DataTask(OTV_CHANNEL, callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncTaskRunnerCallback callback) {
        new DataTask(channel.getUrl(), callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void onRetry(AsyncTaskRunnerCallback callback) {
        Otv.getInstance().onRetry();
        onInit(callback);
    }
}
