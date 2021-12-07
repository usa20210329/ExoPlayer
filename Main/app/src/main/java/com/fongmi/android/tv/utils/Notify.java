package com.fongmi.android.tv.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.databinding.DialogSettingBinding;
import com.fongmi.android.tv.impl.SeekBarListener;
import com.fongmi.android.tv.ui.PlayerActivity;

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

	public static void show(String text) {
		getInstance().makeText(text);
	}

	private void makeText(String message) {
		if (mToast != null) mToast.cancel();
		mToast = Toast.makeText(App.get(), message, Toast.LENGTH_LONG);
		mToast.show();
	}

	public static void showDialog(PlayerActivity context, boolean tv) {
		DialogSettingBinding binding = DialogSettingBinding.inflate(LayoutInflater.from(context));
		new AlertDialog.Builder(context).setView(binding.getRoot()).show();
		binding.control.setVisibility(tv ? View.VISIBLE : View.GONE);
		binding.delay.setProgress(Prefers.getDelay());
		binding.size.setProgress(Prefers.getSize());
		binding.boot.setChecked(Prefers.isBoot());
		binding.full.setChecked(Prefers.isFull());
		binding.pad.setChecked(Prefers.isPad());
		binding.pip.setChecked(Prefers.isPip());
		binding.rev.setChecked(Prefers.isRev());
		binding.draw.check(Prefers.isHdr() ? R.id.surface : R.id.texture);
		binding.boot.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> Prefers.putBoot(isChecked));
		binding.pip.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> Prefers.putPip(isChecked));
		binding.rev.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> Prefers.putRev(isChecked));
		binding.full.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
			Prefers.putFull(isChecked);
			context.setScaleType();
		});
		binding.pad.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
			Prefers.putPad(isChecked);
			context.setKeypad();
		});
		binding.draw.setOnCheckedChangeListener((group, checkedId) -> {
			Prefers.putHdr(checkedId == R.id.surface);
			context.setPlayerView();
		});
		binding.size.setOnSeekBarChangeListener(new SeekBarListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Prefers.putSize(progress);
				context.onSizeChange();
			}
		});
		binding.delay.setOnSeekBarChangeListener(new SeekBarListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Prefers.putDelay(progress);
			}
		});
	}
}
