package com.fongmi.android.ltv.utils;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.impl.KeyDownImpl;

import java.util.ArrayList;
import java.util.List;

public class KeyDown {

	private List<Channel> mChannels;
	private KeyDownImpl mKeyDown;
	private StringBuilder mText;
	private Handler mHandler;
	private ViewGroup mInfo;
	private TextView mNumber;
	private TextView mName;

	public KeyDown(KeyDownImpl keyDown, ViewGroup info, TextView number, TextView name) {
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
		showInfo();
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
		} else if (Utils.isMenuKey(event)) {
			mKeyDown.onKeyCenter(true);
		} else if (Utils.isEnterKey(event)) {
			mKeyDown.onKeyCenter(event.isLongPress());
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

	private void showInfo() {
		mInfo.setVisibility(View.VISIBLE);
		mNumber.setText(mText.toString());
		int index = mChannels.indexOf(Channel.create(mText.toString()));
		boolean exist = index != -1 && !mChannels.get(index).isHidden();
		mName.setVisibility(exist ? View.VISIBLE : View.GONE);
		if (exist) mName.setText(mChannels.get(index).getName());
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
