package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.model.Geo;
import com.fongmi.android.tv.network.AsyncTaskRunner;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.fongmi.android.tv.utils.Utils;

import org.ksoap2.serialization.SoapObject;

import java.util.List;

import static com.fongmi.android.tv.Constant.FREE_GEO;
import static com.fongmi.android.tv.Constant.REGISTER_ID;
import static com.fongmi.android.tv.Constant.REGISTER_IP;
import static com.fongmi.android.tv.Constant.REGISTER_MAC;
import static com.fongmi.android.tv.Constant.TEMP_URI;
import static com.fongmi.android.tv.Constant.USER_ID;
import static com.fongmi.android.tv.Constant.USER_MAC;
import static com.fongmi.android.tv.Constant.WTV_CHANNEL;
import static com.fongmi.android.tv.Constant.WTV_NOTICE;

public class ApiService extends BaseApiService {

    @Override
    public void onInit(AsyncTaskRunnerCallback callback) {
        new WebService(getSoap(WTV_NOTICE)).executeOnExecutor(mExecutor);
        new AsyncTaskRunner(FREE_GEO, getGeo(callback)).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannels(AsyncTaskRunnerCallback callback) {
        Utils.getChannels(getCallback(callback));
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncTaskRunnerCallback callback) {
        callback.onResponse(channel.getUrl());
    }

    @Override
    public void onRetry(AsyncTaskRunnerCallback callback) {
        new AsyncTaskRunner(FREE_GEO, getGeo(callback)).executeOnExecutor(mExecutor);
    }

    private AsyncTaskRunnerCallback getGeo(final AsyncTaskRunnerCallback callback) {
        return new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse(String result) {
                Geo.save(result);
                callback.onResponse();
            }
        };
    }

    private AsyncTaskRunnerCallback getCallback(final AsyncTaskRunnerCallback callback) {
        return new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse(List<Channel> items) {
                new WebService(getSoap(WTV_CHANNEL), getCallback(callback, items)).executeOnExecutor(mExecutor);
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

    private SoapObject getSoap(String name) {
        SoapObject soap = new SoapObject(TEMP_URI, name);
        soap.addProperty(REGISTER_MAC, USER_MAC);
        soap.addProperty(REGISTER_ID, USER_ID);
        soap.addProperty(REGISTER_IP, Geo.get());
        return soap;
    }
}
