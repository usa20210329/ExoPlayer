package com.fongmi.android.library.ltv;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

class Geo {

	@SerializedName("ip")
	private String ip;

	private static Geo objectFrom(String str) {
		return TextUtils.isEmpty(str) ? new Geo() : new Gson().fromJson(str, Geo.class);
	}

	private String getIp() {
		return TextUtils.isEmpty(ip) ? "" : ip;
	}

	static String get(String result) {
		return objectFrom(result).getIp();
	}
}
