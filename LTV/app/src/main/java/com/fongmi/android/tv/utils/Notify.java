package com.fongmi.android.tv.utils;

import android.widget.Toast;

import com.fongmi.android.tv.App;

public class Notify {

    private static class Loader {
        static volatile Notify INSTANCE = new Notify();
    }

    public static Notify getInstance() {
        return Loader.INSTANCE;
    }

    public static void show(int resId) {
        show(Utils.getString(resId));
    }

    public static void show(int resId, int duration) {
        show(Utils.getString(resId), duration);
    }

    public static void show(String text) {
        getInstance().makeText(text, Toast.LENGTH_LONG);
    }

    public static void show(String text, int duration) {
        getInstance().makeText(text, duration);
    }

    private void makeText(String message, int duration) {
        if (message.length() < 3) return;
        Toast.makeText(App.getInstance(), message, duration).show();
    }
}
