package com.fongmi.android.tv.utils;

import android.widget.Toast;

import com.fongmi.android.tv.App;

public class Notify {

    private Toast mToast;

    private static class Loader {
        static volatile Notify INSTANCE = new Notify();
    }

    public static Notify getInstance() {
        return Loader.INSTANCE;
    }

    public static void show(int resId) {
        show(Utils.getString(resId));
    }

    public static void show(String text) {
        getInstance().makeText(text, Toast.LENGTH_SHORT);
    }

    public static void alert(String text) {
        getInstance().makeText(text, Toast.LENGTH_LONG);
    }

    public static void once(String key, int resId) {
        if (!Prefers.getBoolean(key)) {
            Prefers.putBoolean(key, true);
            alert(Utils.getString(resId));
        }
    }

    private void makeText(String message, int duration) {
        if (message.length() < 3) return;
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(App.getInstance(), message, duration);
        mToast.show();
    }
}
