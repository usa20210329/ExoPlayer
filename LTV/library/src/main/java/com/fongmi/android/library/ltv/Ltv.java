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
		return mSample = Code.getSample();
	}
}
