package com.fongmi.android.ltv.bean;

import com.fongmi.android.ltv.utils.Prefers;
import com.fongmi.android.ltv.utils.Utils;

public class Type extends Bean {

	private String name;

	public static Type create(int resId) {
		return new Type(Utils.getString(resId));
	}

	private Type(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTextSize() {
		return Prefers.getSize() * 2 + 16;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Type)) return false;
		Type it = (Type) obj;
		return getName().equals(it.getName());
	}
}
