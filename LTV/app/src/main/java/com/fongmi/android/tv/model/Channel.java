package com.fongmi.android.tv.model;

import android.text.TextUtils;

import com.fongmi.android.tv.utils.Prefers;
import com.google.firebase.database.DataSnapshot;

public class Channel {

	private String number;
	private String name;
	private String url;
	private String real;
	private boolean token;
	private boolean hidden;
	private boolean select;

	public static Channel create(String number) {
		Channel item = new Channel();
		item.setNumber(number);
		return item;
	}

	public static Channel create(DataSnapshot data) {
		return data.getValue(Channel.class);
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return TextUtils.isEmpty(name) ? "" : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return TextUtils.isEmpty(url) ? "" : url;
	}

	public String getReal() {
		return TextUtils.isEmpty(real) ? "" : real;
	}

	public void setReal(String real) {
		this.real = real;
	}

	public boolean isToken() {
		return token;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isSelect() {
		return select;
	}

	private void setSelect(boolean select) {
		this.select = select;
	}

	public void deselect() {
		setSelect(false);
	}

	public void select() {
		setSelect(true);
	}

	public boolean hasUrl() {
		return getReal().length() > 0;
	}

	public int getTextSize() {
		return Prefers.getSize() * 2 + 14;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Channel)) return false;
		Channel it = (Channel) obj;
		return getNumber().equals(it.getNumber());
	}
}
