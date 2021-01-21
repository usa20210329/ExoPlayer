package com.fongmi.android.ltv.bean;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class Config {

	private List<Channel> channel;
	private String provider;
	private String key;
	private String url;
	private Core core;
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

	public Core getCore() {
		return core;
	}

	public int getVersion() {
		return version;
	}

	public String getAuth() {
		return getCore().getAuth();
	}

	public String getName() {
		return getCore().getName();
	}

	public String getPass() {
		return getCore().getPass();
	}

	private static class Core {

		private String auth;
		private String name;
		private String pass;

		public String getAuth() {
			return auth;
		}

		public String getName() {
			return name;
		}

		public String getPass() {
			return pass;
		}
	}
}
