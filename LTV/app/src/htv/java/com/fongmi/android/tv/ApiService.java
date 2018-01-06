package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApiService extends BaseApiService {

    @Override
    public void getChannels(final AsyncTaskRunnerCallback callback) {
        FirebaseDatabase.getInstance().getReference().child("channel").addValueEventListener(new AsyncTaskRunnerCallback() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                List<Channel> items = new ArrayList<>();
                while (iterator.hasNext()) items.add(iterator.next().getValue(Channel.class));
                callback.onResponse(items);
            }
        });
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncTaskRunnerCallback callback) {
        callback.onResponse(channel.getUrl());
    }

    @Override
    public void onRetry(AsyncTaskRunnerCallback callback) {
        callback.onResponse();
    }
}
