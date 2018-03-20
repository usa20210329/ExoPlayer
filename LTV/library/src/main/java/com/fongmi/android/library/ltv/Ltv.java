package com.fongmi.android.library.ltv;

import android.text.TextUtils;

import com.fongmi.android.library.ltv.model.Geo;
import com.fongmi.android.library.ltv.model.Item;
import com.fongmi.android.library.ltv.utils.Utils;

import org.ksoap2.serialization.SoapObject;

import static com.fongmi.android.library.ltv.Constant.*;

public class Ltv {

    private String mIp;
    private String mId;
    private String mToken;

    private static class Loader {
        static volatile Ltv INSTANCE = new Ltv();
    }

    public static Ltv getInstance() {
        return Loader.INSTANCE;
    }

    private Ltv() {
        this.mId = USER_ID;
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

    public String getGeo() {
        return mIp = Geo.get(Utils.getResult());
    }

    public void setId(String id) {
        this.mId = TextUtils.isEmpty(id) ? USER_ID : id;
    }

    private SoapObject getSoap(String name) {
        SoapObject soap = new SoapObject(TEMP_URI, name);
        soap.addProperty(REGISTER_MAC, USER_MAC);
        soap.addProperty(REGISTER_ID, mId);
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
