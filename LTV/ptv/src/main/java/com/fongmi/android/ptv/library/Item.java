package com.fongmi.android.ptv.library;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

class Item {

    @SerializedName("isEncrypt")
    private boolean isEncrypt;
    @SerializedName("data")
    private String data;

    static Item objectFrom(String str) {
        return new Gson().fromJson(str, Item.class);
    }

    private boolean isEncrypt() {
        return isEncrypt;
    }

    String getData() {
        return isEncrypt() ? Utils.getEncryption(data) : data;
    }
}
