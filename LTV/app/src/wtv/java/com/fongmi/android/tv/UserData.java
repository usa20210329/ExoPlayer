package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Geo;

import org.ksoap2.serialization.SoapObject;

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
}
