package com.fongmi.android.ltv.utils;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.fongmi.android.ltv.App;
import com.fongmi.android.ltv.R;
import com.fongmi.android.ltv.impl.SeekBarListener;
import com.fongmi.android.ltv.ui.MainActivity;

public class Notify {

	private Toast mToast;

	private static class Loader {
		static volatile Notify INSTANCE = new Notify();
	}

	private static Notify getInstance() {
		return Loader.INSTANCE;
	}

	public static void show(int resId) {
		show(Utils.getString(resId));
	}

	static void show(String text) {
		getInstance().makeText(text);
	}

	private void makeText(String message) {
		if (mToast != null) mToast.cancel();
		mToast = Toast.makeText(App.get(), message, Toast.LENGTH_LONG);
		mToast.show();
	}

	public static void showDialog(MainActivity context) {
		showDialog(context, View.GONE);
	}

	public static void showDialog(MainActivity context, int visible) {
		AlertDialog dialog = new AlertDialog.Builder(context).setView(R.layout.view_setting).show();
		dialog.findViewById(R.id.control).setVisibility(visible);
		SeekBar size = dialog.findViewById(R.id.size);
		SeekBar delay = dialog.findViewById(R.id.delay);
		CheckBox boot = dialog.findViewById(R.id.boot);
		CheckBox full = dialog.findViewById(R.id.full);
		CheckBox pad = dialog.findViewById(R.id.pad);
		CheckBox rev = dialog.findViewById(R.id.rev);
		CheckBox ok = dialog.findViewById(R.id.ok);
		delay.setProgress(Prefers.getDelay());
		size.setProgress(Prefers.getSize());
		boot.setChecked(Prefers.isBoot());
		full.setChecked(Prefers.isFull());
		pad.setChecked(Prefers.isPad());
		rev.setChecked(Prefers.isRev());
		ok.setChecked(Prefers.isOk());
		boot.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> Prefers.putBoot(isChecked));
		rev.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> Prefers.putRev(isChecked));
		ok.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> Prefers.putOk(isChecked));
		full.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
			Prefers.putFull(isChecked);
			context.setScaleType();
		});
		pad.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
			Prefers.putPad(isChecked);
			context.setKeypad();
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
