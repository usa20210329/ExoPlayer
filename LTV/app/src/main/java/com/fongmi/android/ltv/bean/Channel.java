package com.fongmi.android.ltv.bean;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.fongmi.android.ltv.App;
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
	private String epg;
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

	public String getEpg() {
		return epg;
	}

	public void setEpg(String epg) {
		this.epg = epg;
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

	public void loadImage(ImageView view) {
		Glide.with(App.get()).load(Token.getUrl().concat(getLogo())).transition(DrawableTransitionOptions.withCrossFade()).into(view);
	}

	public void putKeep() {
		if (!isHidden()) Prefers.putKeep(getNumber());
	}

	public Channel get() {
		Channel item = new Channel();
		item.setNumber(getNumber());
		item.setName(getName());
		item.setLogo(getLogo());
		item.setEpg(getEpg());
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
