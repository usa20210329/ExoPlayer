package com.fongmi.android.ltv.impl;

import com.fongmi.android.ltv.bean.Channel;

public interface KeyDownImpl {

	void onFind(Channel item);

	void onKeyVertical(boolean isNext);

	void onKeyLeft();

	void onKeyRight();

	void onKeyCenter();

	void onKeyBack();

	void onLongPress();
}
