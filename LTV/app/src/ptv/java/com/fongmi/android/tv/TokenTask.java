package com.fongmi.android.tv;

import android.os.AsyncTask;

import com.fongmi.android.ptv.library.Ptv;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;

class TokenTask extends AsyncTask<Void, Integer, String> {

    private AsyncTaskRunnerCallback callback;
    private Channel channel;

    static void execute(Channel channel, AsyncTaskRunnerCallback callback) {
        new TokenTask(channel, callback).execute();
    }

    private TokenTask(Channel channel, AsyncTaskRunnerCallback callback) {
        this.channel = channel;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        return channel.getUrl().contains("?token=") ? Ptv.getToken(channel.getUrl(), channel.getName()) : "";
    }

    @Override
    protected void onPostExecute(String result) {
        callback.onResponse(channel.getUrl().concat(result));
    }
}
