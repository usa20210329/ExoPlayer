package com.fongmi.android.tv.utils;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fongmi.android.tv.impl.KeyDownImpl;
import com.fongmi.android.tv.model.Channel;

import java.util.ArrayList;
import java.util.List;

public class KeyDown {

	private List<Channel> mChannels;
	private KeyDownImpl mKeyDown;
	private StringBuilder mText;
	private Handler mHandler;
	private LinearLayout mInfo;
	private TextView mNumber;
	private TextView mName;

	public KeyDown(KeyDownImpl keyDown, LinearLayout info, TextView number, TextView name) {
		this.mKeyDown = keyDown;
		this.mNumber = number;
		this.mName = name;
		this.mInfo = info;
		this.init();
	}

	private void init() {
		if (mChannels == null) mChannels = new ArrayList<>();
		if (mHandler == null) mHandler = new Handler();
		if (mText == null) mText = new StringBuilder();
	}

	public void setChannels(List<Channel> items) {
		mChannels.clear();
		mChannels.addAll(items);
	}

	public boolean onKeyDown(int keyCode) {
		mText.append(getNumber(keyCode));
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, getDelay());
		showChannelInfo();
		return true;
	}

	public boolean onKeyDown(KeyEvent event) {
		if (Utils.isUpKey(event)) {
			mKeyDown.onKeyVertical(Prefers.isRev());
		} else if (Utils.isDownKey(event)) {
			mKeyDown.onKeyVertical(!Prefers.isRev());
		} else if (Utils.isLeftKey(event)) {
			mKeyDown.onKeyHorizontal(true);
		} else if (Utils.isRightKey(event)) {
			mKeyDown.onKeyHorizontal(false);
		} else if (Utils.isEnterKey(event)) {
			mKeyDown.onKeyCenter();
		} else if (Utils.isBackKey(event)) {
			mKeyDown.onKeyBack();
		}
		return true;
	}

	private int getDelay() {
		return Prefers.getDelay() * 500 + 500;
	}

	private int getNumber(int keyCode) {
		return Utils.isNumberPad(keyCode) ? keyCode - 144 : keyCode - 7;
	}

	private void showChannelInfo() {
		mInfo.setVisibility(View.VISIBLE);
		mNumber.setText(mText.toString());
		Channel channel = Channel.create(mText.toString());
		boolean isExist = mChannels.contains(channel);
		mName.setVisibility(isExist ? View.VISIBLE : View.GONE);
		if (isExist) mName.setText(mChannels.get(mChannels.indexOf(channel)).getName());
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mKeyDown.onFind(Channel.create(mText.toString()));
			mInfo.setVisibility(View.GONE);
			mName.setVisibility(View.GONE);
			mName.setText("");
			mText.setLength(0);
		}
	};
}
