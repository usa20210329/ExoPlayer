package com.fongmi.android.ltv.impl;

import com.fongmi.android.ltv.model.Channel;

public interface KeyDownImpl {

	void onFind(Channel item);

	void onKeyVertical(boolean isNext);

	void onKeyHorizontal(boolean isLeft);

	void onKeyCenter(boolean isLongPress);

	void onKeyBack();
}
