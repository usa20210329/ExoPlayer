package com.fongmi.android.tv;

import android.os.AsyncTask;

import com.fongmi.android.ltv.library.Ltv;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.utils.Notify;

import static com.fongmi.android.ltv.library.Constant.*;

class WebService extends AsyncTask<Void, Integer, String> {

    private AsyncTaskRunnerCallback callback;
    private String action;
    private int number;

    WebService(String action) {
        this.action = action;
    }

    WebService(String action, AsyncTaskRunnerCallback callback) {
        this.action = action;
        this.callback = callback;
    }

    WebService(String action, int number, AsyncTaskRunnerCallback callback) {
        this.action = action;
        this.number = number;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        switch (action) {
            case LTV_GEO:
                return Ltv.getGeo();
            case LTV_NOTICE:
                return Ltv.getNotice();
            case LTV_CHANNEL:
                return Ltv.getChannel();
            default:
                return Ltv.getUrl(number);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        switch (action) {
            case LTV_GEO:
                callback.onResponse();
                break;
            case LTV_NOTICE:
                Notify.alert(result);
                break;
            case LTV_CHANNEL:
                callback.onResponse(Channel.arrayFrom(result));
                break;
            case LTV_CHANNEL_URL:
                callback.onResponse(result);
                break;
        }
    }
}