package com.fongmi.android.tv.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.R;

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
        getInstance().makeText(text);
    }

    private void makeText(String message) {
        if (message.length() < 3) return;
        if (mToast != null) mToast.cancel();
        mToast = new Toast(App.getInstance());
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(getLayout(message));
        mToast.show();
    }

    private View getLayout(String message) {
        View layout = LayoutInflater.from(App.getInstance()).inflate(R.layout.view_toast, null, false);
        TextView view = layout.findViewById(R.id.text);
        layout.setAlpha(0.9f);
        view.setText(message);
        return layout;
    }
}
