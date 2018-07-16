package com.fongmi.android.library.ltv;

public class Ltv {

	private String mSample;

	private static class Loader {
		static volatile Ltv INSTANCE = new Ltv();
	}

	public static Ltv getInstance() {
		return Loader.INSTANCE;
	}

	public String getUrl(String url) {
		return url.startsWith("http") ? Utils.getRealUrl(url) : mSample.replace("m3u8", url);
	}

	public String getSample() {
		String result = Utils.getResult(Code.getSample());
		String domain = result.substring(0, result.lastIndexOf("/") + 1);
		String param = result.substring(result.lastIndexOf("?"), result.length());
		return mSample = domain + "m3u8" + param;
	}
}
