package com.fongmi.android.tv.utils;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.fongmi.android.tv.impl.KeyDownImpl;
import com.fongmi.android.tv.model.Channel;

public class KeyDown {

    private StringBuilder mText;
    private KeyDownImpl mKeyDown;
    private Handler mHandler;
    private TextView mNumber;

    public KeyDown(KeyDownImpl keyDown, TextView number) {
        this.mKeyDown = keyDown;
        this.mNumber = number;
    }

    public boolean onKeyDown(int keyCode) {
        if (mHandler == null) mHandler = new Handler();
        if (mText == null) mText = new StringBuilder();
        mText.append(getNumber(keyCode));
        mNumber.setText(mText.toString());
        mNumber.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, getDelay());
        return true;
    }

    public boolean onKeyDown(KeyEvent event) {
        if (Utils.isUpKey(event)) {
            mKeyDown.onKeyVertical(true);
        } else if (Utils.isDownKey(event)) {
            mKeyDown.onKeyVertical(false);
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

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mKeyDown.onFind(Channel.create(mNumber.getText().toString()));
            mNumber.setVisibility(View.GONE);
            mNumber.setText("");
            mText.setLength(0);
        }
    };
}
