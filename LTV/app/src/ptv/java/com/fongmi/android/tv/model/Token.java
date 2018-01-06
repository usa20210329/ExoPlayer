package com.fongmi.android.tv.model;

import android.text.TextUtils;

import com.fongmi.android.tv.utils.Prefers;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("Token")
    private String token;

    private static Token objectFrom(String str) {
        return new Gson().fromJson(str, Token.class);
    }

    private String getToken() {
        return TextUtils.isEmpty(token) ? "" : token;
    }

    private static String getKey(boolean five) {
        return five ? "token59" : "token61";
    }

    public static void save(boolean five, String result) {
        Prefers.putString(getKey(five), objectFrom(result).getToken());
    }

    public static String getUrl(boolean five, String url) {
        return url.contains("?token=") ? url.concat(Prefers.getString(getKey(five))) : url;
    }
}
