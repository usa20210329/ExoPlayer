package com.fongmi.android.ltv.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fongmi.android.ltv.utils.Prefers;
import com.google.firebase.database.DataSnapshot;

@Entity
public class Channel {

	@NonNull
	@PrimaryKey
	private String number;
	private String name;
	private String url;
	private boolean token;
	private boolean hidden;
	private boolean dynamic;
	@Ignore private boolean select;

	public static Channel create(String number) {
		return new Channel(number);
	}

	public static Channel create(DataSnapshot data) {
		return data.getValue(Channel.class);
	}

	public Channel() {
		this("");
	}

	public Channel(@NonNull String number) {
		this.number = number;
	}

	@NonNull
	public String getNumber() {
		return number;
	}

	public void setNumber(@NonNull String number) {
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

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isToken() {
		return token;
	}

	public void setToken(boolean token) {
		this.token = token;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
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

	public int getTextSize() {
		return Prefers.getSize() * 2 + 14;
	}

	public Channel get() {
		Channel item = new Channel();
		item.setNumber(getNumber());
		item.setName(getName());
		item.setUrl(getUrl());
		item.setToken(isToken());
		item.setHidden(isHidden());
		return item;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Channel)) return false;
		Channel it = (Channel) obj;
		return getNumber().equals(it.getNumber());
	}
}
