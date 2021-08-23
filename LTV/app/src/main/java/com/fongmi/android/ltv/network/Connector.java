package com.fongmi.android.ltv.network;

import com.fongmi.android.ltv.utils.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Connector {

	private static final String VND_APPLE_URL = "vnd.apple.mpegurl";
	private static final String FORCE_DOWN = "force-download";

	private HttpURLConnection conn;
	private final String url;

	public static Connector link(String url) throws IOException {
		return new Connector(url).connect();
	}

	public Connector(String url) {
		this.url = url;
	}

	public Connector connect() throws IOException {
		conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		conn.setUseCaches(false);
		conn.setDoInput(false);
		conn.setDoInput(true);
		conn.connect();
		return this;
	}

	public String getResult() throws IOException {
		String line;
		StringBuilder result = new StringBuilder();
		InputStream in = new BufferedInputStream(conn.getInputStream());
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		while ((line = bufferedReader.readLine()) != null) result.append(line);
		in.close();
		return result.toString();
	}

	public String getPath() throws IOException {
		if (isRedirect()) return conn.getHeaderField("Location");
		if (isFile()) return download();
		return url;
	}

	public String download() throws IOException {
		File cacheFile = FileUtil.getCacheFile("index.m3u8");
		InputStream is = conn.getInputStream();
		int bytesRead;
		byte[] buffer = new byte[4096];
		FileOutputStream os = new FileOutputStream(cacheFile);
		while (((bytesRead = is.read(buffer)) != -1)) os.write(buffer, 0, bytesRead);
		os.close();
		is.close();
		return cacheFile.getPath();
	}

	public boolean isRedirect() throws IOException {
		return conn.getResponseCode() / 100 == 3;
	}

	public boolean isFile() {
		return conn.getContentType().contains(VND_APPLE_URL) || conn.getContentType().contains(FORCE_DOWN);
	}
}
