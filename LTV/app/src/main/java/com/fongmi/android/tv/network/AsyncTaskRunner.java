package com.fongmi.android.tv.network;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncTaskRunner extends AsyncTask<Void, Integer, String> {

    private AsyncTaskRunnerCallback callback;
    private HttpURLConnection conn;
    private String url;

    public AsyncTaskRunner(String url, AsyncTaskRunnerCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            StringBuilder sb = new StringBuilder();
            int count;
            char[] buf = new char[1024];
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            while ((count = isr.read(buf)) != -1) {
                sb.append(new String(buf, 0, count));
            }
            is.close();
            isr.close();
            return sb.toString();
        } catch (IOException e) {
            return "";
        } finally {
            conn.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.length() > 0) callback.onResponse(result);
    }
}