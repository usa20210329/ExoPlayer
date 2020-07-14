package com.fongmi.android.ltv.network;

import android.os.AsyncTask;
import android.webkit.URLUtil;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.utils.FileUtil;
import com.fongmi.android.ltv.utils.Token;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Task extends AsyncTask<Channel, Integer, String> {

	private AsyncCallback callback;
	private HttpURLConnection conn;

	Task(AsyncCallback callback, Channel item) {
		this.callback = callback;
		this.execute(item);
	}

	@Override
	protected String doInBackground(Channel... items) {
		try {
			String url = items[0].getUrl();
			if (items[0].isToken()) url = url.concat(Token.get());
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setUseCaches(false);
			conn.setDoInput(false);
			conn.setDoInput(true);
			conn.connect();
			return checkContent(url);
		} catch (Exception e) {
			return items[0].getUrl();
		} finally {
			if (conn != null) conn.disconnect();
		}
	}

	private String checkContent(String url) throws IOException {
		if (conn.getContentType().contains("force-download")) return startDownload();
		return conn.getResponseCode() / 100 == 3 ? conn.getHeaderField("Location") : url;
	}

	private String startDownload() throws IOException {
		String disposition = conn.getHeaderField("Content-Disposition");
		String fileName = URLUtil.guessFileName(null, disposition, null);
		File cacheFile = FileUtil.getCacheFile(fileName);
		InputStream is = conn.getInputStream();
		int bytesRead;
		byte[] buffer = new byte[1024];
		FileOutputStream os = new FileOutputStream(cacheFile);
		while (!isCancelled() && ((bytesRead = is.read(buffer)) != -1)) os.write(buffer, 0, bytesRead);
		os.close();
		is.close();
		return cacheFile.getPath();
	}

	@Override
	protected void onPostExecute(String url) {
		callback.onResponse(url);
	}
}