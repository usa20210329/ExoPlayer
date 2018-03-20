package com.fongmi.android.library.ltv;

import com.fongmi.android.library.ltv.model.Geo;
import com.fongmi.android.library.ltv.model.Item;
import com.fongmi.android.library.ltv.utils.Utils;

import org.ksoap2.serialization.SoapObject;

import static com.fongmi.android.library.ltv.Constant.*;

public class Ltv {

    private String mIp;
    private String mToken;
    private String mSample;

    private static class Loader {
        static volatile Ltv INSTANCE = new Ltv();
    }

    public static Ltv getInstance() {
        return Loader.INSTANCE;
    }

    public String getNotice() {
        return Utils.getResult(getSoap(LTV_NOTICE));
    }

    public String onLogin(String... params) {
        try {
            return mToken = Utils.getResult(getSoap(params[0], params[1])).split("\n")[2];
        } catch (Exception e) {
            return mToken = null;
        }
    }

    public String getChannel() {
        return Item.getChannels(Utils.getResult(getSoap(LTV_CHANNEL)));
    }

    public String getUrl(int number) {
        return Utils.getResult(getSoap(number));
    }

    public String getUrl(String m3u8) {
        return mSample.replace("m3u8", m3u8);
    }

    public String getGeo() {
        return mIp = Geo.get(Utils.getResult());
    }

    public String getSample() {
        String result = Utils.getResult(getSoap(1));
        String domain = result.substring(0, result.lastIndexOf("/") + 1);
        String param = result.substring(result.lastIndexOf("?"), result.length());
        return mSample = domain + "m3u8" + param;
    }

    private SoapObject getSoap(String name) {
        SoapObject soap = new SoapObject(TEMP_URI, name);
        soap.addProperty(REGISTER_MAC, USER_MAC);
        soap.addProperty(REGISTER_ID, USER_ID);
        soap.addProperty(REGISTER_IP, mIp);
        return soap;
    }

    private SoapObject getSoap(String mail, String pwd) {
        SoapObject soap = new SoapObject(TEMP_URI, LOGON);
        soap.addProperty(REGISTER_EMAIL, mail);
        soap.addProperty(REGISTER_PASSWORD, pwd);
        return soap;
    }

    private SoapObject getSoap(int number) {
        SoapObject soap = getSoap(LTV_CHANNEL_URL);
        soap.addProperty(LOGON_TOKEN, mToken);
        soap.addProperty(CHANNEL_NO, number);
        return soap;
    }
}
