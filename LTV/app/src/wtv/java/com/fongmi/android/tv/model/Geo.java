package com.fongmi.android.tv.model;

import android.text.TextUtils;

import com.fongmi.android.tv.utils.Prefers;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Geo {

    @SerializedName("ip")
    private String ip;

    private static Geo objectFrom(String str) {
        return TextUtils.isEmpty(str) ? new Geo() : new Gson().fromJson(str, Geo.class);
    }

    private String getIp() {
        return TextUtils.isEmpty(ip) ? "" : ip;
    }

    public static void save(String result) {
        Prefers.putIp(objectFrom(result).getIp());
    }

    public static String get() {
        return Prefers.getIp();
    }
}
