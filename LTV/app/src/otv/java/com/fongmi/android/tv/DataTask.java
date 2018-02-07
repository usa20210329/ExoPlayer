package com.fongmi.android.tv;

import android.os.AsyncTask;

import com.fongmi.android.library.otv.Otv;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;

import static com.fongmi.android.library.otv.Constant.*;

class DataTask extends AsyncTask<Void, Integer, String> {

    private AsyncTaskRunnerCallback callback;
    private String action;

    DataTask(String action, AsyncTaskRunnerCallback callback) {
        this.action = action;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        switch (action) {
            case OTV_INFO:
                return Otv.getInstance().getInfo();
            case OTV_CHANNEL:
                return Otv.getInstance().getChannel();
            default:
                return Otv.getInstance().getUrl(action);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        switch (action) {
            case OTV_INFO:
                callback.onResponse();
                break;
            case OTV_CHANNEL:
                callback.onResponse(Channel.arrayFrom(result));
                break;
            default:
                callback.onResponse(result);
                break;
        }
    }
}