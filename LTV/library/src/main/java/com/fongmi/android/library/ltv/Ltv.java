package com.fongmi.android.library.ltv;

public class Ltv {

	private static class Loader {
		static volatile Ltv INSTANCE = new Ltv();
	}

	public static Ltv getInstance() {
		return Loader.INSTANCE;
	}

	public String getUrl(String url) {
		return url.startsWith("http") ? Utils.getRealUrl(url) : Code.getSample().replace("m3u8", url);
	}
}
