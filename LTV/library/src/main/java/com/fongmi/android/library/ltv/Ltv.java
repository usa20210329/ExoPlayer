package com.fongmi.android.library.ltv;

import java.net.HttpURLConnection;
import java.net.URL;

public class Ltv {

	private static class Loader {
		static volatile Ltv INSTANCE = new Ltv();
	}

	public static Ltv getInstance() {
		return Loader.INSTANCE;
	}

	public String getUrl(String url) {
		return getRealUrl(url);
	}

	private static String getRealUrl(String url) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			conn.getInputStream();
			boolean redirect = conn.getResponseCode() / 100 == 3;
			return redirect ? conn.getHeaderField("Location") : url;
		} catch (Exception e) {
			return url;
		}
	}
}
