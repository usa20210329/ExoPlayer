package com.fongmi.android.library.ltv;

import org.ksoap2.serialization.SoapObject;

import static com.fongmi.android.library.ltv.Constant.*;

public class Ltv {

	private String mIp;
	private String mSample;

	private static class Loader {
		static volatile Ltv INSTANCE = new Ltv();
	}

	public static Ltv getInstance() {
		return Loader.INSTANCE;
	}

	private Ltv() {
		this.mIp = Geo.get(Utils.getResult());
	}

	public String getUrl(String url) {
		return url.startsWith("http") ? Utils.getRealUrl(url) : mSample.replace("m3u8", url);
	}

	public String getSample() {
		String result = Utils.getResult(getSoap());
		String domain = result.substring(0, result.lastIndexOf("/") + 1);
		String param = result.substring(result.lastIndexOf("?"), result.length());
		return mSample = domain + "m3u8" + param;
	}

	private SoapObject getSoap() {
		SoapObject soap = getSoap(LTV_CHANNEL_URL);
		soap.addProperty(CHANNEL_NO, 2);
		return soap;
	}

	private SoapObject getSoap(String name) {
		SoapObject soap = new SoapObject(TEMP_URI, name);
		soap.addProperty(REGISTER_MAC, USER_MAC);
		soap.addProperty(REGISTER_ID, USER_ID);
		soap.addProperty(REGISTER_IP, mIp);
		return soap;
	}
}
