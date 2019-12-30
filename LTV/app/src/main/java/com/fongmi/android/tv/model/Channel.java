package com.fongmi.android.tv.model;

import android.text.TextUtils;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.utils.Prefers;
import com.fongmi.android.tv.utils.Utils;
import com.google.firebase.database.DataSnapshot;

public class Channel {

	private int number;
	private String name;
	private String url;
	private String real;
	private boolean token;
	private boolean hidden;
	private boolean select;

	public static Channel create(String number) {
		return create(Integer.valueOf(number));
	}

	public static Channel create(int number) {
		Channel channel = new Channel();
		channel.setNumber(number);
		return channel;
	}

	public static Channel create(DataSnapshot data) {
		return data.getValue(Channel.class);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
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

	public String getInfo() {
		return Utils.getString(R.string.channel_number, getNumber()) + "　" + getName();
	}

	public int getTextSize() {
		return Prefers.getSize() * 2 + 14;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Channel)) return false;
		Channel it = (Channel) obj;
		return getNumber() == it.getNumber();
	}
}
