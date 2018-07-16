package com.fongmi.android.library.ltv;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.HttpURLConnection;
import java.net.URL;

class Utils {

	static String getResult(SoapObject soap) {
		try {
			SoapSerializationEnvelope soapserializationenvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			soapserializationenvelope.bodyOut = soap;
			soapserializationenvelope.dotNet = true;
			soapserializationenvelope.setOutputSoapObject(soap);
			HttpTransportSE trans = new HttpTransportSE(Constant.URL, 30000);
			String action = soap.getNamespace() + soap.getName();
			trans.call(action, soapserializationenvelope);
			return soapserializationenvelope.getResponse().toString();
		} catch (Exception e) {
			return "";
		}
	}

	static String getRealUrl(String url) {
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
