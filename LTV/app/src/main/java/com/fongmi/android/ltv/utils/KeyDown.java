package com.fongmi.android.ltv.utils;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.databinding.ViewWidgetBinding;
import com.fongmi.android.ltv.impl.KeyDownImpl;

import java.util.ArrayList;
import java.util.List;

public class KeyDown {

	private final ViewWidgetBinding mWidget;
	private final KeyDownImpl mKeyDown;
	private List<Channel> mChannels;
	private StringBuilder mText;
	private Handler mHandler;
	private boolean mPress;

	public KeyDown(KeyDownImpl keyDown, ViewWidgetBinding widget) {
		this.mKeyDown = keyDown;
		this.mWidget = widget;
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
		if (mText.length() >= 3) return false;
		mText.append(getNumber(keyCode));
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, getDelay());
		showInfo();
		return true;
	}

	public boolean onKeyDown(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && Utils.isUpKey(event)) {
			mKeyDown.onKeyVertical(Prefers.isRev());
		} else if (event.getAction() == KeyEvent.ACTION_DOWN && Utils.isDownKey(event)) {
			mKeyDown.onKeyVertical(!Prefers.isRev());
		} else if (event.getAction() == KeyEvent.ACTION_UP && Utils.isLeftKey(event)) {
			mKeyDown.onKeyLeft();
		} else if (event.getAction() == KeyEvent.ACTION_UP && Utils.isRightKey(event)) {
			mKeyDown.onKeyRight();
		} else if (event.getAction() == KeyEvent.ACTION_UP && Utils.isBackKey(event)) {
			mKeyDown.onKeyBack();
		} else if (event.getAction() == KeyEvent.ACTION_UP && Utils.isMenuKey(event)) {
			mKeyDown.onLongPress();
		} else if (Utils.isEnterKey(event)) {
			checkPress(event);
		}
		return true;
	}

	private void checkPress(KeyEvent event) {
		if (event.isLongPress()) {
			mPress = true;
			mKeyDown.onLongPress();
		} else if (event.getAction() == KeyEvent.ACTION_UP) {
			if (mPress) mPress = false;
			else mKeyDown.onKeyCenter();
		}
	}

	private int getDelay() {
		return Prefers.getDelay() * 500 + 500;
	}

	private int getNumber(int keyCode) {
		return Utils.isNumberPad(keyCode) ? keyCode - 144 : keyCode - 7;
	}

	private void showInfo() {
		mWidget.info.setVisibility(View.VISIBLE);
		mWidget.number.setText(mText.toString());
		int index = mChannels.indexOf(Channel.create(mText.toString()));
		boolean exist = index != -1 && !mChannels.get(index).isHidden();
		mWidget.name.setVisibility(exist ? View.VISIBLE : View.GONE);
		if (exist) mWidget.name.setText(mChannels.get(index).getName());
	}

	private final Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mKeyDown.onFind(Channel.create(mText.toString()));
			mWidget.info.setVisibility(View.GONE);
			mWidget.name.setVisibility(View.GONE);
			mWidget.name.setText("");
			mText.setLength(0);
		}
	};
}
