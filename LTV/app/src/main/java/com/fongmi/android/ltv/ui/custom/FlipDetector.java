package com.fongmi.android.ltv.ui.custom;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.fongmi.android.ltv.impl.KeyDownImpl;

public class FlipDetector implements GestureDetector.OnGestureListener {

	private static final int DISTANCE = 50;
	private final KeyDownImpl mKeyDown;

	public static GestureDetector create(Context context) {
		return new GestureDetector(context, new FlipDetector((KeyDownImpl) context));
	}

	public FlipDetector(KeyDownImpl keyDown) {
		this.mKeyDown = keyDown;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		mKeyDown.onTapUp();
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (Math.abs(e1.getY() - e2.getY()) < DISTANCE) {
			mKeyDown.onSeek((int) (e2.getX() - e1.getX()) * 100);
			return true;
		} else if (e1.getY() - e2.getY() > DISTANCE) {
			mKeyDown.onFlip(true);
			return true;
		} else if (e2.getY() - e1.getY() > DISTANCE) {
			mKeyDown.onFlip(false);
			return true;
		} else {
			return false;
		}
	}
}
