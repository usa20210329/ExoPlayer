package com.fongmi.android.tv;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.fongmi.android.ptv.library.Ptv;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;

class JsTask extends AsyncTask<Void, Integer, String> {

    private AsyncTaskRunnerCallback callback;

    static void execute(AsyncTaskRunnerCallback callback) {
        new JsTask(callback).execute();
    }

    private JsTask(AsyncTaskRunnerCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        return Ptv.getJs();
    }

    @Override
    protected void onPostExecute(String result) {
        setJavaScript(result);
        callback.onResponse();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setJavaScript(String result) {
        WebView webView = new WebView(App.getInstance());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadDataWithBaseURL(null, result, "text/html", "UTF-8", null);
    }
}
