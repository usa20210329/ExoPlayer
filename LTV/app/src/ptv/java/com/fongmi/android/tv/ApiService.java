package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.model.Token;
import com.fongmi.android.tv.network.AsyncTaskRunner;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.fongmi.android.tv.Constant.TOKEN_59;
import static com.fongmi.android.tv.Constant.TOKEN_61;
import static com.fongmi.android.tv.Constant.SERVER;

public class ApiService extends BaseApiService {

    private boolean five;

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
        setFive(channel.getUrl().contains(SERVER));
        callback.onResponse(Token.getUrl(five, channel.getUrl()));
    }

    @Override
    public void onRetry(AsyncTaskRunnerCallback callback) {
        new AsyncTaskRunner(five ? TOKEN_59 : TOKEN_61, getCallback(callback)).execute();
    }

    private void setFive(boolean five) {
        this.five = five;
    }

    private AsyncTaskRunnerCallback getCallback(final AsyncTaskRunnerCallback callback) {
        return new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse(String result) {
                Token.save(five, result);
                callback.onResponse();
            }
        };
    }
}
