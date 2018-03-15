package com.fongmi.android.tv;

import android.app.Application;

public class App extends Application {

    private static App instance;

    public App() {
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    public static boolean isTwo() {
        return BuildConfig.FLAVOR.equals("l2tv");
    }
}
