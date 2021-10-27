package com.fongmi.android.ltv.impl;

public interface KeyDownImpl {

	void onShow(String number);

	void onFind(String number);

	void onFlip(boolean up);

	void onKeyVertical(boolean next);

	void onKeyLeft();

	void onKeyRight();

	void onKeyCenter();

	void onKeyMenu();

	void onKeyBack();

	void onLongPress();
}
