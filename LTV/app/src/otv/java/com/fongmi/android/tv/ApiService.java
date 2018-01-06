package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.model.Info;
import com.fongmi.android.tv.network.AsyncTaskRunner;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.fongmi.android.tv.utils.Encrypt;

import static com.fongmi.android.tv.Constant.CHANNEL;
import static com.fongmi.android.tv.Constant.INFO;

public class ApiService extends BaseApiService {

    private int retryTimes;

    @Override
    public void onInit(AsyncTaskRunnerCallback callback) {
        new AsyncTaskRunner(INFO, getCallBack(callback)).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannels(AsyncTaskRunnerCallback callback) {
        new AsyncTaskRunner(CHANNEL, getCallback(callback)).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncTaskRunnerCallback callback) {
        callback.onResponse(Encrypt.getRealUrl(channel.getUrl()));
    }

    @Override
    public void onRetry(AsyncTaskRunnerCallback callback) {
        if (++retryTimes > 5) {
            retryTimes = 0;
            new AsyncTaskRunner(INFO, getCallBack(callback)).executeOnExecutor(mExecutor);
        } else {
            UserData.getInstance().setTsHx();
            callback.onResponse();
        }
    }

    private AsyncTaskRunnerCallback getCallBack(final AsyncTaskRunnerCallback callback) {
        return new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse(String result) {
                UserData.getInstance().setInfo(Info.objectFrom(result));
                UserData.getInstance().setTsHx();
                callback.onResponse();
            }
        };
    }

    private AsyncTaskRunnerCallback getCallback(final AsyncTaskRunnerCallback callback) {
        return new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse(String result) {
                callback.onResponse(Channel.arrayFrom(result));
            }
        };
    }
}
