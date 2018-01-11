package com.fongmi.android.tv.utils;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
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
        getInstance().makeText(text, Toast.LENGTH_SHORT);
    }

    public static void alert(String text) {
        getInstance().makeText(text, Toast.LENGTH_LONG);
    }

    private void makeText(String message, int duration) {
        if (message.length() < 3) return;
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(App.getInstance(), message, duration);
        mToast.show();
    }

    public static void showDialog(Activity activity) {
        new MaterialDialog.Builder(activity)
                .theme(Theme.LIGHT)
                .title(R.string.channel_wait)
                .items(R.array.select_wait)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        Prefers.putWait(which == 0);
                        return false;
                    }
                }).show();
    }
}
