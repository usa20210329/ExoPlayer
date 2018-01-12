package com.fongmi.android.tv.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fongmi.android.tv.App;
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.impl.SeekBarListener;
import com.fongmi.android.tv.ui.ChannelActivity;

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

    public static void showDialog(ChannelActivity context) {
        showDialog(context, View.GONE);
    }

    public static void showDialog(final ChannelActivity context, int visibility) {
        MaterialDialog dialog = new MaterialDialog.Builder(context).customView(R.layout.view_setting, true).show();
        ViewGroup view = dialog.getCustomView().findViewById(R.id.control);
        SeekBar size = dialog.getCustomView().findViewById(R.id.size);
        SeekBar delay = dialog.getCustomView().findViewById(R.id.delay);
        Switch back = dialog.getCustomView().findViewById(R.id.back);
        Switch play = dialog.getCustomView().findViewById(R.id.play);
        view.setVisibility(visibility);
        size.setProgress(Prefers.getSize());
        delay.setProgress(Prefers.getDelay());
        back.setChecked(Prefers.isBackWait());
        play.setChecked(Prefers.isPlayWait());
        size.setOnSeekBarChangeListener(new SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                context.onSizeChange(progress);
            }
        });
        delay.setOnSeekBarChangeListener(new SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Prefers.putDelay(progress);
            }
        });
        back.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefers.putBackWait(isChecked);
            }
        });
        play.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefers.putPlayWait(isChecked);
            }
        });
    }
}
