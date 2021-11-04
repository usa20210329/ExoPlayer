package com.fongmi.android.tv.impl;

public interface KeyDownImpl {

	void onTapUp();

	void onShow(String number);

	void onFind(String number);

	void onFlip(boolean up);

	void onSeek(boolean forward);

	void onKeyVertical(boolean next);

	void onKeyLeft();

	void onKeyRight();

	void onKeyCenter();

	void onKeyMenu();

	void onKeyBack();

	void onLongPress();
}
