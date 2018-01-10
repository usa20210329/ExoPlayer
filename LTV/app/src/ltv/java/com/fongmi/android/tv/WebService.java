package com.fongmi.android.tv;

import android.os.AsyncTask;
import android.util.Base64;

import com.fongmi.android.tv.model.Geo;
import com.fongmi.android.tv.model.Root;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.utils.Notify;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.MessageDigest;

import static com.fongmi.android.tv.Constant.LTV_CHANNEL;
import static com.fongmi.android.tv.Constant.LTV_CHANNEL_URL;
import static com.fongmi.android.tv.Constant.LTV_NOTICE;
import static com.fongmi.android.tv.Constant.URL;

class WebService extends AsyncTask<Void, Integer, String> {

    private AsyncTaskRunnerCallback callback;
    private SoapObject soap;
    private String action;

    WebService(SoapObject soap, AsyncTaskRunnerCallback callback) {
        this.action = soap.getNamespace() + soap.getName();
        this.callback = callback;
        this.soap = soap;
    }

    WebService(SoapObject soap) {
        this.action = soap.getNamespace() + soap.getName();
        this.soap = soap;
    }

    @Override
    protected String doInBackground(Void... params) {
        SoapSerializationEnvelope soapserializationenvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapserializationenvelope.bodyOut = soap;
        soapserializationenvelope.dotNet = true;
        soapserializationenvelope.setOutputSoapObject(soap);
        HttpTransportSE trans = new HttpTransportSE(URL, 30000);
        try {
            trans.call(action, soapserializationenvelope);
            return soapserializationenvelope.getResponse().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        switch (soap.getName()) {
            case LTV_NOTICE:
                Notify.show(result);
                break;
            case LTV_CHANNEL:
                callback.onResponse(Root.getChannels(result));
                break;
            case LTV_CHANNEL_URL:
                callback.onResponse(getRealUrl(result));
                break;
        }
    }

    private String getRealUrl(String url) {
        try {
            int index = url.indexOf("ex=") + 3;
            String ex = url.substring(index);
            String key = "1Qaw3esZx" + Geo.get() + ex;
            key = Base64.encodeToString(MessageDigest.getInstance("MD5").digest(key.getBytes()), 0);
            key = key.replace("+", "-").replace("/", "_").replace("=", "").replaceAll("\n", "");
            return url.concat("&st=").concat(key);
        } catch (Exception e) {
            return url;
        }
    }
}