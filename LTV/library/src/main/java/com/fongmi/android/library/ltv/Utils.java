package com.fongmi.android.library.ltv;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	static String getResult() {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(Constant.GEO).openConnection();
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
		}
	}
}
