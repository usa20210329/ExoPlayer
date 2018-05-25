package com.fongmi.android.tv.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fongmi.android.tv.App;

public class Prefers {

	private static final String SIZE = "size";
	private static final String DELAY = "delay";
	static final String MAIL = "mail";
	static final String PWD = "pwd";
	static final String KEEP = "keep";
	static final String BACK_WAIT = "back_wait";
	static final String PLAY_WAIT = "play_wait";

	private static SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(App.getInstance());
	}

	private static String getString(String key) {
		return getPreferences().getString(key, "");
	}

	static void putString(String key, String value) {
		getPreferences().edit().putString(key, value).apply();
	}

	private static Integer getInt(String key, int defaultValue) {
		return getPreferences().getInt(key, defaultValue);
	}

	private static void putInt(String key, int value) {
		getPreferences().edit().putInt(key, value).apply();
	}

	private static Boolean getBoolean(String key) {
		return getPreferences().getBoolean(key, false);
	}

	static void putBoolean(String key, boolean value) {
		getPreferences().edit().putBoolean(key, value).apply();
	}

	public static int getSize() {
		return getInt(SIZE, 0);
	}

	public static void putSize(int value) {
		putInt(SIZE, value);
	}

	static int getDelay() {
		return getInt(DELAY, 1);
	}

	static void putDelay(int value) {
		putInt(DELAY, value);
	}

	public static boolean isKeep() {
		return getBoolean(KEEP);
	}

	public static boolean isBackWait() {
		return getBoolean(BACK_WAIT);
	}

	public static boolean isPlayWait() {
		return getBoolean(PLAY_WAIT);
	}

	public static String getMail() {
		return getString(MAIL);
	}

	public static String getPwd() {
		return getString(PWD);
	}
}
