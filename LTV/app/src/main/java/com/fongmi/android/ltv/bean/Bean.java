package com.fongmi.android.ltv.bean;

import androidx.room.Ignore;

public class Bean {

	@Ignore
	private boolean select;

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}
}
