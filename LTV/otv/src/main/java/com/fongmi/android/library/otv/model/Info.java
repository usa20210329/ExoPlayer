package com.fongmi.android.library.otv.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Info {

    @SerializedName("key")
    private String key;
    @SerializedName("ip")
    private String ip;
    @SerializedName("iv")
    private String iv;
    @SerializedName("salt")
    private String salt;
    @SerializedName("ts")
    private long ts;

    public static Info objectFrom(String str) {
        return new Gson().fromJson(str, Info.class);
    }

    String getKey() {
        return key;
    }

    String getIp() {
        return ip;
    }

    String getIv() {
        return iv;
    }

    String getSalt() {
        return salt;
    }

    long getTs() {
        return ts;
    }
}
