package com.fongmi.android.ltv.utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.fongmi.android.ltv.App;

public class Prefers {

	private static final String SIZE = "size";
	private static final String DELAY = "delay";
	private static final String BOOT = "boot";
	private static final String FULL = "full";
	private static final String KEEP = "keep";
	private static final String REV = "rev";

	private static SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(App.get());
	}

	private static String getString(String key, String defaultValue) {
		return getPreferences().getString(key, defaultValue);
	}

	private static void putString(String key, String value) {
		getPreferences().edit().putString(key, value).apply();
	}

	private static int getInt(String key, int defaultValue) {
		return getPreferences().getInt(key, defaultValue);
	}

	private static void putInt(String key, int value) {
		getPreferences().edit().putInt(key, value).apply();
	}

	private static boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	private static boolean getBoolean(String key, boolean defaultValue) {
		return getPreferences().getBoolean(key, defaultValue);
	}

	private static void putBoolean(String key, boolean value) {
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

	public static boolean isBoot() {
		return getBoolean(BOOT);
	}

	static void putBoot(boolean value) {
		putBoolean(BOOT, value);
	}

	public static boolean isFull() {
		return getBoolean(FULL, true);
	}

	static void putFull(boolean value) {
		putBoolean(FULL, value);
	}

	public static String getKeep() {
		return getString(KEEP, "");
	}

	public static void putKeep(String value) {
		putString(KEEP, value);
	}

	static boolean isRev() {
		return getBoolean(REV);
	}

	static void putRev(boolean value) {
		putBoolean(REV, value);
	}
}
