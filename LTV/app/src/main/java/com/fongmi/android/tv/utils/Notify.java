package com.fongmi.android.tv.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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
        SeekBar size = dialog.getCustomView().findViewById(R.id.size);
        SeekBar delay = dialog.getCustomView().findViewById(R.id.delay);
        ViewGroup view = dialog.getCustomView().findViewById(R.id.control);
        RadioGroup back = dialog.getCustomView().findViewById(R.id.back);
        RadioGroup play = dialog.getCustomView().findViewById(R.id.play);
        RadioButton backWait = dialog.getCustomView().findViewById(R.id.back_wait);
        RadioButton playWait = dialog.getCustomView().findViewById(R.id.play_wait);
        view.setVisibility(visibility);
        size.setProgress(Prefers.getSize());
        delay.setProgress(Prefers.getDelay());
        backWait.setChecked(Prefers.isBackWait());
        playWait.setChecked(Prefers.isPlayWait());
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
        back.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Prefers.putBackWait(checkedId == R.id.back_wait);
            }
        });
        play.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Prefers.putPlayWait(checkedId == R.id.play_wait);
            }
        });
    }
}
