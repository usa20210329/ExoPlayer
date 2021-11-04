package com.fongmi.android.tv.ui.custom;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import androidx.appcompat.widget.AppCompatTextView;

public class ScrollTextView extends AppCompatTextView {

	private static final int DURATION = 30 * 1000;
	private Scroller mScroller;

	public ScrollTextView(Context context) {
		this(context, null);
	}

	public ScrollTextView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.textViewStyle);
	}

	public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setSingleLine();
		setEllipsize(null);
		setVisibility(INVISIBLE);
	}

	public void startScroll() {
		int width = -1 * getWidth();
		setHorizontallyScrolling(true);
		setScroller(mScroller = new Scroller(getContext(), new LinearInterpolator()));
		int scrollingLen = calculateScrollingLen();
		int distance = scrollingLen - (getWidth() + width);
		int duration = (Double.valueOf(DURATION * distance * 1.0 / scrollingLen)).intValue();
		mScroller.startScroll(width, 0, distance, 0, duration);
		setVisibility(VISIBLE);
		invalidate();
	}

	private int calculateScrollingLen() {
		TextPaint paint = getPaint();
		Rect rect = new Rect();
		String text = getText().toString();
		paint.getTextBounds(text, 0, text.length(), rect);
		return rect.width() + getWidth();
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller == null) return;
		if (mScroller.isFinished()) {
			mScroller.abortAnimation();
			setVisibility(GONE);
		}
	}
}