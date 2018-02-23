package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.fongmi.android.tv.utils.Prefers;
import com.fongmi.android.tv.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class ApiService extends BaseApiService {

    private static final String KEY = "hami";

    @Override
    public void onInit(AsyncCallback callback) {
        getToken(callback);
    }

    @Override
    public void getChannels(AsyncCallback callback) {
        Utils.getChannels(callback);
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncCallback callback) {
        callback.onResponse(getUrl(channel.getUrl()));
    }

    @Override
    public void onRetry(AsyncCallback callback) {
        callback.onResponse();
    }

    private void getToken(final AsyncCallback callback) {
        FirebaseDatabase.getInstance().getReference().child(KEY).addValueEventListener(new AsyncCallback() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Prefers.putString(KEY, dataSnapshot.getValue().toString());
                callback.onResponse();
            }
        });
    }

    private String getUrl(String url) {
        if (url.startsWith("http://dm500xyz.ddns.net:8138/and/hami/")) {
            return url.replace(KEY, Prefers.getString(KEY));
        } else if (url.startsWith("http://58.99.33.2:1935/")) {
            return url.concat("?checkCode=37050688asdfsdfsadf&as=654321&aa=825623&dr=");
        } else {
            return url;
        }
    }
}
