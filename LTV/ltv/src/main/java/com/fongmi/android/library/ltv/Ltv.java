package com.fongmi.android.library.ltv;

import android.text.TextUtils;
import android.util.Base64;

import com.fongmi.android.library.ltv.model.Geo;
import com.fongmi.android.library.ltv.model.Item;
import com.fongmi.android.library.ltv.utils.Utils;

import org.ksoap2.serialization.SoapObject;

import static com.fongmi.android.library.ltv.Constant.*;

public class Ltv {

    private String mIp;
    private String mId;

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

    public String getChannel() {
        return Item.getChannels(Utils.getResult(getSoap(LTV_CHANNEL)));
    }

    public String getUrl(int number) {
        return getRealUrl(Utils.getResult(getSoap(number)));
    }

    public String getGeo() {
        return mIp = Geo.get(Utils.getResult());
    }

    public void setId(String id) {
        this.mId = TextUtils.isEmpty(id) ? USER_ID : id;
    }

    private String getRealUrl(String url) {
        int index = url.indexOf("ex=") + 3;
        String ex = url.substring(index);
        String key = "1Qaw3esZx" + mIp + ex;
        key = Base64.encodeToString(Utils.getMd5().digest(key.getBytes()), 0);
        key = key.replace("+", "-").replace("/", "_").replace("=", "").replaceAll("\n", "");
        return url.concat("&st=").concat(key);
    }

    private SoapObject getSoap(String name) {
        SoapObject soap = new SoapObject(TEMP_URI, name);
        soap.addProperty(REGISTER_MAC, USER_MAC);
        soap.addProperty(REGISTER_ID, mId);
        soap.addProperty(REGISTER_IP, mIp);
        return soap;
    }

    private SoapObject getSoap(int number) {
        return getSoap(LTV_CHANNEL_URL).addProperty(CHANNEL_NO, number);
    }
}
