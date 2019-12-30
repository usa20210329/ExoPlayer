package com.fongmi.android.tv.impl;

import com.fongmi.android.tv.model.Channel;

public interface KeyDownImpl {

	void onFind(Channel item);

	void onKeyVertical(boolean isNext);

	void onKeyHorizontal(boolean isLeft);

	void onKeyCenter();

	void onKeyBack();
}
