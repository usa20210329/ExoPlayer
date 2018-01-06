package com.fongmi.android.tv.model;

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

    public String getKey() {
        return key;
    }

    public String getIp() {
        return ip;
    }

    public String getIv() {
        return iv;
    }

    public String getSalt() {
        return salt;
    }

    public long getTs() {
        return ts;
    }
}
