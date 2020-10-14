package com.fongmi.android.ltv.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fongmi.android.ltv.utils.Prefers;
import com.fongmi.android.ltv.utils.Token;

import java.util.Locale;

@Entity
public class Channel extends Bean {

	@NonNull
	@PrimaryKey
	private String number;
	private String name;
	private String logo;
	private String url;
	private String provider;
	private boolean token;
	private boolean hidden;
	private boolean dynamic;

	public static Channel create(String number) {
		return new Channel(String.format(Locale.getDefault(), "%03d", Integer.valueOf(number)));
	}

	public Channel() {
		this("");
	}

	@Ignore
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

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getUrl() {
		return TextUtils.isEmpty(url) ? "" : url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProvider() {
		return TextUtils.isEmpty(provider) ? Token.getProvider() : provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
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

	public boolean isTvBus() {
		return getUrl().startsWith("tvbus://");
	}

	public boolean isForce() {
		return getUrl().startsWith("p8p://");
	}

	public int getTextSize() {
		return Prefers.getSize() * 2 + 14;
	}

	public String getLogoUrl() {
		return getLogo().startsWith("http") ? getLogo() : getLogo().isEmpty() ? "" : Token.getUrl().concat(getLogo());
	}

	public Channel get() {
		Channel item = new Channel();
		item.setNumber(getNumber());
		item.setName(getName());
		item.setLogo(getLogo());
		item.setUrl(getUrl());
		item.setToken(isToken());
		item.setHidden(isHidden());
		item.setDynamic(isDynamic());
		item.setProvider(getProvider());
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
