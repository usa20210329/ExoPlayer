package com.fongmi.android.ltv.library;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

class Geo {

    @SerializedName("ip")
    private String ip;

    static Geo objectFrom(String str) {
        return TextUtils.isEmpty(str) ? new Geo() : new Gson().fromJson(str, Geo.class);
    }

    String getIp() {
        return TextUtils.isEmpty(ip) ? "" : ip;
    }
}
