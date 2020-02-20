package com.fongmi.android.ltv.bean;

import java.util.List;

public class Config {

	private List<Channel> channel;
	private String provider;
	private String key;
	private String url;
	private int version;

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
