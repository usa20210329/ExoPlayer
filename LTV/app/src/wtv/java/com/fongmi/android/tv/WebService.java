package com.fongmi.android.tv;

import android.os.AsyncTask;

import com.fongmi.android.tv.model.Root;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.utils.Notify;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import static com.fongmi.android.tv.Constant.URL;
import static com.fongmi.android.tv.Constant.WTV_CHANNEL;
import static com.fongmi.android.tv.Constant.WTV_NOTICE;

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
            case WTV_NOTICE:
                Notify.alert(result);
                break;
            case WTV_CHANNEL:
                callback.onResponse(Root.getChannels(result));
                break;
        }
    }
}