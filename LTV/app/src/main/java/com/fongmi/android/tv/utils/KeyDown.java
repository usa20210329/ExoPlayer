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
        mText.append(keyCode - 7);
        mNumber.setText(mText.toString());
        mNumber.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 1000);
        return true;
    }

    public boolean onKeyDown(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            mKeyDown.onKeyVertical(true);
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            mKeyDown.onKeyVertical(false);
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            mKeyDown.onKeyHorizontal(true);
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mKeyDown.onKeyHorizontal(false);
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            mKeyDown.onKeyCenter();
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mKeyDown.onKeyBack();
        }
        return true;
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
