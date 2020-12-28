package com.fongmi.android.ltv.impl;

public interface KeyDownImpl {

	void onShow(String number);

	void onFind(String number);

	void onKeyVertical(boolean isNext);

	void onKeyLeft();

	void onKeyRight();

	void onKeyCenter();

	void onKeyBack();

	void onLongPress();
}
