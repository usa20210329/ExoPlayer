package com.fongmi.android.library.ltv;

import com.l2tv.ltv.NativeApp;

import org.ksoap2.serialization.SoapObject;

import static com.fongmi.android.library.ltv.Constant.*;

public class Ltv {

	private String mIp;
	private String mSample;

	private static class Loader {
		static volatile Ltv INSTANCE = new Ltv();
	}

	private Ltv() {
		NativeApp.loadLibrary();
	}

	public static Ltv getInstance() {
		return Loader.INSTANCE;
	}

	public String getNotice() {
		return Utils.getResult(getSoap(LTV_NOTICE));
	}

	public String getUrl(String url) {
		return url.startsWith("http") ? url : getRealUrl(mSample.replace("m3u8", url));
	}

	public String getGeo() {
		return mIp = Geo.get(Utils.getResult());
	}

	public String getSample() {
		String result = Utils.getResult(getSoap());
		String domain = result.substring(0, result.lastIndexOf("/") + 1);
		String param = result.substring(result.lastIndexOf("?"), result.length());
		return mSample = domain + "m3u8" + param;
	}

	private SoapObject getSoap() {
		SoapObject soap = getSoap(LTV_CHANNEL_URL);
		soap.addProperty(CHANNEL_NO, 1);
		return soap;
	}

	private SoapObject getSoap(String name) {
		SoapObject soap = new SoapObject(TEMP_URI, name);
		soap.addProperty(REGISTER_MAC, USER_MAC);
		soap.addProperty(REGISTER_ID, USER_ID);
		soap.addProperty(REGISTER_IP, mIp);
		return soap;
	}

	private String getRealUrl(String url) {
		int index = url.indexOf("ex=") + 3;
		String key = NativeApp.getinfo64(url.substring(index));
		key = key.replace("+", "-").replace("/", "_").replace("=", "").replaceAll("\n", "").replaceAll("\r", "");
		return url.concat("&st=").concat(key);
	}
}
