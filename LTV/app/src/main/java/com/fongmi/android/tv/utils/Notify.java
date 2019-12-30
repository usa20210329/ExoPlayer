package com.fongmi.android.tv.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.impl.SeekBarListener;
import com.fongmi.android.tv.ui.ChannelActivity;

public class Notify {

	private Toast mToast;

	private static class Loader {
		static volatile Notify INSTANCE = new Notify();
	}

	private static Notify getInstance() {
		return Loader.INSTANCE;
	}

	public static void show(int resId) {
		getInstance().makeText(Utils.getString(resId));
	}

	private void makeText(String message) {
		if (mToast != null) mToast.cancel();
		mToast = Toast.makeText(App.getInstance(), message, Toast.LENGTH_SHORT);
		mToast.show();
	}

	public static void showDialog(ChannelActivity context) {
		showDialog(context, View.GONE);
	}

	public static void showDialog(final ChannelActivity context, int visibility) {
		AlertDialog dialog = new AlertDialog.Builder(context).setView(R.layout.view_setting).show();
		ViewGroup control = dialog.findViewById(R.id.control);
		SeekBar size = dialog.findViewById(R.id.size);
		SeekBar delay = dialog.findViewById(R.id.delay);
		CheckBox enter = dialog.findViewById(R.id.enter);
		CheckBox boot = dialog.findViewById(R.id.boot);
		CheckBox full = dialog.findViewById(R.id.full);
		CheckBox rev = dialog.findViewById(R.id.rev);
		control.setVisibility(visibility);
		size.setProgress(Prefers.getSize());
		delay.setProgress(Prefers.getDelay());
		enter.setChecked(Prefers.isEnter());
		boot.setChecked(Prefers.isBoot());
		full.setChecked(Prefers.isFull());
		rev.setChecked(Prefers.isRev());
		enter.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> Prefers.putEnter(isChecked));
		boot.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> Prefers.putBoot(isChecked));
		rev.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> Prefers.putRev(isChecked));
		full.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
			Prefers.putFull(isChecked);
			context.setScaleType();
		});
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
	}
}
