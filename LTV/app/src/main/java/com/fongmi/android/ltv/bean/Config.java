package com.fongmi.android.ltv.bean;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class Config {

	private List<Channel> channel;
	private String provider;
	private String key;
	private String url;
	private int version;

	public static Config get(DataSnapshot data) {
		return data.getValue(Config.class);
	}

	public List<Channel> getChannel() {
		return channel;
	}

	public String getProvider() {
		return provider;
	}

	public String getKey() {
		return key;
	}

	public String getUrl() {
		return url;
	}

	public int getVersion() {
		return version;
	}
}
