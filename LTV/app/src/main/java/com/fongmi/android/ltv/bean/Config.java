package com.fongmi.android.ltv.bean;

import com.google.firebase.database.DataSnapshot;

public class Config {

	private String key;
	private String url;
	private String provider;
	private int version;

	public static Config create(DataSnapshot data) {
		return data.getValue(Config.class);
	}

	public String getKey() {
		return key;
	}

	public String getUrl() {
		return url;
	}

	public String getProvider() {
		return provider;
	}

	public int getVersion() {
		return version;
	}
}
