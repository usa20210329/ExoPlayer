package com.fongmi.android.ltv.bean;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class Config {

	private List<Type> type;
	private String notice;
	private String url;
	private Core core;
	private int version;

	public static Config get(DataSnapshot data) {
		return data.getValue(Config.class);
	}

	public List<Type> getType() {
		return type;
	}

	public String getNotice() {
		return notice;
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

	public static class Core {

		private String broker;
		private String auth;
		private String name;
		private String pass;

		public String getBroker() {
			return broker;
		}

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
