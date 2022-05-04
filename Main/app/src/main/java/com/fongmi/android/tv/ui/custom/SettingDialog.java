package com.fongmi.android.tv.ui.custom;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.databinding.DialogSettingBinding;
import com.fongmi.android.tv.impl.SeekBarListener;
import com.fongmi.android.tv.ui.PlayerActivity;
import com.fongmi.android.tv.utils.Prefers;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SettingDialog extends BottomSheetDialogFragment {

	private DialogSettingBinding binding;
	private final PlayerActivity context;
	private final boolean tv;

	public static void show(PlayerActivity context, boolean tv) {
		for (Fragment fragment : context.getSupportFragmentManager().getFragments()) if (fragment instanceof BottomSheetDialogFragment) return;
		new SettingDialog(context, tv).show(context.getSupportFragmentManager(), null);
	}

	private SettingDialog(PlayerActivity context, boolean tv) {
		this.context = context;
		this.tv = tv;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = DialogSettingBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		dialog.setOnShowListener((DialogInterface f) -> setBehavior(dialog));
		return dialog;
	}

	private void setBehavior(BottomSheetDialog dialog) {
		FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
		BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
		behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
		behavior.setSkipCollapsed(true);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
		initEvent();
	}

	protected void initView() {
		binding.delay.setProgress(Prefers.getDelay());
		binding.size.setProgress(Prefers.getSize());
		binding.boot.setChecked(Prefers.isBoot());
		binding.full.setChecked(Prefers.isFull());
		binding.pad.setChecked(Prefers.isPad());
		binding.pip.setChecked(Prefers.isPip());
		binding.rev.setChecked(Prefers.isRev());
		binding.draw.check(Prefers.isHdr() ? R.id.surface : R.id.texture);
		binding.pad.setVisibility(tv ? View.GONE : View.VISIBLE);
		binding.pip.setVisibility(tv ? View.GONE : View.VISIBLE);
		binding.rev.setVisibility(tv ? View.VISIBLE : View.GONE);
		binding.boot.setVisibility(tv ? View.VISIBLE : View.GONE);
	}

	protected void initEvent() {
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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}
