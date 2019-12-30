package com.fongmi.android.tv.model;

import android.text.TextUtils;

import com.fongmi.android.tv.utils.Prefers;

public class Channel {

	private String number;
	private String name;
	private String url;
	private String real;
	private boolean token;
	private boolean select;

	public static Channel create(String number) {
		Channel channel = new Channel();
		channel.setNumber(number);
		return channel;
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
		return Prefers.getSize() * 2 + 16;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Channel)) return false;
		Channel it = (Channel) obj;
		return getNumber().equals(it.getNumber());
	}
}
