package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.model.Geo;
import com.fongmi.android.tv.network.AsyncTaskRunner;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.fongmi.android.tv.utils.Utils;

import org.ksoap2.serialization.SoapObject;

import static com.fongmi.android.tv.Constant.CHANNEL_NO;
import static com.fongmi.android.tv.Constant.FREE_GEO;
import static com.fongmi.android.tv.Constant.REGISTER_ID;
import static com.fongmi.android.tv.Constant.REGISTER_IP;
import static com.fongmi.android.tv.Constant.REGISTER_MAC;
import static com.fongmi.android.tv.Constant.TEMP_URI;
import static com.fongmi.android.tv.Constant.USER_ID;
import static com.fongmi.android.tv.Constant.USER_MAC;
import static com.fongmi.android.tv.Constant.WTV_CHANNEL;
import static com.fongmi.android.tv.Constant.WTV_CHANNEL_URL;
import static com.fongmi.android.tv.Constant.WTV_NOTICE;

public class ApiService extends BaseApiService {

    @Override
    public void onInit(AsyncTaskRunnerCallback callback) {
        new WebService(getSoap(WTV_NOTICE)).executeOnExecutor(mExecutor);
        new AsyncTaskRunner(FREE_GEO, getCallback(callback)).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannels(AsyncTaskRunnerCallback callback) {
        new WebService(getSoap(WTV_CHANNEL), callback).executeOnExecutor(mExecutor);
        Utils.getChannels(callback);
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncTaskRunnerCallback callback) {
        if (channel.getNumber() > 3000) callback.onResponse(channel.getUrl());
        else new WebService(getSoap(channel), callback).executeOnExecutor(mExecutor);
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

    private SoapObject getSoap(String name) {
        SoapObject soap = new SoapObject(TEMP_URI, name);
        soap.addProperty(REGISTER_MAC, USER_MAC);
        soap.addProperty(REGISTER_ID, USER_ID);
        soap.addProperty(REGISTER_IP, Geo.get());
        return soap;
    }

    private SoapObject getSoap(Channel channel) {
        return getSoap(WTV_CHANNEL_URL).addProperty(CHANNEL_NO, channel.getNumber());
    }
}
