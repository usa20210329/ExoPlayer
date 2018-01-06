package com.fongmi.android.tv;

import android.util.Base64;

import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.model.Geo;

import org.ksoap2.serialization.SoapObject;

import java.security.MessageDigest;

import static com.fongmi.android.tv.Constant.CHANNEL_NO;
import static com.fongmi.android.tv.Constant.LTV_CHANNEL_URL;
import static com.fongmi.android.tv.Constant.REGISTER_ID;
import static com.fongmi.android.tv.Constant.REGISTER_IP;
import static com.fongmi.android.tv.Constant.REGISTER_MAC;
import static com.fongmi.android.tv.Constant.TEMP_URI;
import static com.fongmi.android.tv.Constant.USER_ID;
import static com.fongmi.android.tv.Constant.USER_MAC;

class UserData {

    private static class Loader {
        static volatile UserData INSTANCE = new UserData();
    }

    static UserData getInstance() {
        return Loader.INSTANCE;
    }

    SoapObject getSoap(String name) {
        SoapObject soap = new SoapObject(TEMP_URI, name);
        soap.addProperty(REGISTER_MAC, USER_MAC);
        soap.addProperty(REGISTER_ID, USER_ID);
        soap.addProperty(REGISTER_IP, Geo.get());
        return soap;
    }

    SoapObject getSoap(Channel channel) {
        return getSoap(LTV_CHANNEL_URL).addProperty(CHANNEL_NO, channel.getNumber());
    }

    String getRealUrl(String url) {
        try {
            int index = url.indexOf("ex=") + 3;
            String ex = url.substring(index);
            String key = "1Qaw3esZx" + Geo.get() + ex;
            key = Base64.encodeToString(MessageDigest.getInstance("MD5").digest(key.getBytes()), 0);
            key = key.replace("+", "-").replace("/", "_").replace("=", "").replaceAll("\n", "");
            return url.concat("&st=").concat(key);
        } catch (Exception e) {
            return url;
        }
    }
}
