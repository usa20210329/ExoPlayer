package com.fongmi.android.ltv.library;

import android.util.Base64;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class Ltv {

    private static final String URL = "http://132.148.82.117/ltv/ltv_service.asmx";
    private static final String GEO = "http://freegeoip.net/json/";
    private static final String TEMP_URI = "http://tempuri.org/";
    private static final String CHANNEL_NO = "CHANNEL_NO";
    private static final String REGISTER_ID = "REGISTER_ID";
    private static final String REGISTER_IP = "REGISTER_IP";
    private static final String REGISTER_MAC = "REGISTER_MAC";
    private static final String LTV_NOTICE = "LTV_NOTICE";
    private static final String LTV_CHANNEL = "LTV_CHANNEL";
    private static final String LTV_CHANNEL_URL = "LTV_CHANNEL_URL";
    private static final String USER_MAC = "02:00:00:00:00:00";
    private static final String USER_ID = "34550490704212";
    private static String mIp = "";

    public static String getNotice() {
        return getResult(getSoap(LTV_NOTICE));
    }

    public static String getChannel() {
        return Item.getChannels(getResult(getSoap(LTV_CHANNEL)));
    }

    public static String getUrl(int number) {
        return getRealUrl(getResult(getSoap(number)));
    }

    public static String getGeo() {
        Geo geo = Geo.objectFrom(getResult());
        mIp = geo.getIp();
        return mIp;
    }

    private static String getRealUrl(String url) {
        try {
            int index = url.indexOf("ex=") + 3;
            String ex = url.substring(index);
            String key = "1Qaw3esZx" + mIp + ex;
            key = Base64.encodeToString(MessageDigest.getInstance("MD5").digest(key.getBytes()), 0);
            key = key.replace("+", "-").replace("/", "_").replace("=", "").replaceAll("\n", "");
            return url.concat("&st=").concat(key);
        } catch (Exception e) {
            return url;
        }
    }

    private static String getResult(SoapObject soap) {
        try {
            SoapSerializationEnvelope soapserializationenvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapserializationenvelope.bodyOut = soap;
            soapserializationenvelope.dotNet = true;
            soapserializationenvelope.setOutputSoapObject(soap);
            HttpTransportSE trans = new HttpTransportSE(URL, 30000);
            String action = soap.getNamespace() + soap.getName();
            trans.call(action, soapserializationenvelope);
            return soapserializationenvelope.getResponse().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getResult() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(GEO).openConnection();
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

    private static SoapObject getSoap(String name) {
        SoapObject soap = new SoapObject(TEMP_URI, name);
        soap.addProperty(REGISTER_MAC, USER_MAC);
        soap.addProperty(REGISTER_ID, USER_ID);
        soap.addProperty(REGISTER_IP, mIp);
        return soap;
    }

    private static SoapObject getSoap(int number) {
        return getSoap(LTV_CHANNEL_URL).addProperty(CHANNEL_NO, number);
    }
}
