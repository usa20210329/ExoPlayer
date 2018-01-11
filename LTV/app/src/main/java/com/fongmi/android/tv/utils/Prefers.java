package com.fongmi.android.tv.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fongmi.android.tv.App;

public class Prefers {

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    }

    public static String getString(String key) {
        return getPreferences().getString(key, "");
    }

    public static void putString(String key, String value) {
        getPreferences().edit().putString(key, value).apply();
    }

    public static boolean hasWait() {
        return getPreferences().contains("wait");
    }

    public static boolean isWait() {
        return getPreferences().getBoolean("wait", false);
    }

    public static void putWait(boolean value) {
        getPreferences().edit().putBoolean("wait", value).apply();
    }

    public static int getTextSize() {
        return getPreferences().getInt("size", 0);
    }

    public static void putTextSize(int value) {
        getPreferences().edit().putInt("size", value).apply();
    }
}
