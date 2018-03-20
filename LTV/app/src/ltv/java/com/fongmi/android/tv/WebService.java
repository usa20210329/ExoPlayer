package com.fongmi.android.tv;

import android.os.AsyncTask;
import android.util.Patterns;

import com.fongmi.android.library.ltv.Ltv;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncCallback;
import com.fongmi.android.tv.utils.Notify;
import com.fongmi.android.tv.utils.Prefers;

import static com.fongmi.android.library.ltv.Constant.*;

class WebService extends AsyncTask<Void, Integer, String> {

    private AsyncCallback callback;
    private String action;
    private int number;

    WebService(String action) {
        this.action = action;
    }

    WebService(String action, AsyncCallback callback) {
        this.action = action;
        this.callback = callback;
    }

    WebService(String action, int number, AsyncCallback callback) {
        this.action = action;
        this.number = number;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        switch (action) {
            case LTV_GEO:
                return Ltv.getInstance().getGeo();
            case LTV_NOTICE:
                return Ltv.getInstance().getNotice();
            case LTV_LOGON:
                return Ltv.getInstance().onLogin(Prefers.getMail(), Prefers.getPwd());
            case LTV_CHANNEL:
                return Ltv.getInstance().getChannel();
            default:
                return Ltv.getInstance().getUrl(number);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        switch (action) {
            case LTV_GEO:
                callback.onResponse(true);
                break;
            case LTV_NOTICE:
                Notify.alert(result);
                break;
            case LTV_CHANNEL:
                callback.onResponse(Channel.arrayFrom(result));
                break;
            case LTV_CHANNEL_URL:
                checkUrl(result);
                break;
        }
    }

    private void checkUrl(String result) {
        if (Patterns.WEB_URL.matcher(result).matches()) {
            callback.onResponse(result);
        } else {
            callback.onError(result);
        }
    }
}