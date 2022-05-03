package com.fongmi.android.tv.utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.fongmi.android.tv.App;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.lodz.android.mmsplayer.ijk.media.IRenderView;

public class Prefers {

	private static final String DELAY = "delay";
	private static final String BOOT = "boot";
	private static final String FULL = "full";
	private static final String KEEP = "keep";
	private static final String SIZE = "size";
	private static final String PAD = "pad";
	private static final String PIP = "pip";
	private static final String REV = "rev";
	private static final String HDR = "hdr";
	private static final String EXO = "exo";

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

	public static String getKeep() {
		return getString(KEEP, "");
	}

	public static void putKeep(String value) {
		putString(KEEP, value);
	}

	public static int getSize() {
		return getInt(SIZE, 0);
	}

	public static void putSize(int value) {
		putInt(SIZE, value);
	}

	public static int getDelay() {
		return getInt(DELAY, 1);
	}

	public static void putDelay(int value) {
		putInt(DELAY, value);
	}

	public static boolean isBoot() {
		return getBoolean(BOOT);
	}

	public static void putBoot(boolean value) {
		putBoolean(BOOT, value);
	}

	public static boolean isFull() {
		return getBoolean(FULL, true);
	}

	public static void putFull(boolean value) {
		putBoolean(FULL, value);
	}

	public static boolean isPad() {
		return getBoolean(PAD);
	}

	public static void putPad(boolean value) {
		putBoolean(PAD, value);
	}

	public static boolean isPip() {
		return getBoolean(PIP);
	}

	public static void putPip(boolean value) {
		putBoolean(PIP, value);
	}

	public static boolean isRev() {
		return getBoolean(REV);
	}

	public static void putRev(boolean value) {
		putBoolean(REV, value);
	}

	public static boolean isHdr() {
		return getBoolean(HDR);
	}

	public static void putHdr(boolean value) {
		putBoolean(HDR, value);
	}

	public static boolean isExo() {
		return getBoolean(EXO, true);
	}

	public static boolean isIjk() {
		return !isExo();
	}

	public static void putExo(boolean value) {
		putBoolean(EXO, value);
	}

	public static int getRatio() {
		if (isExo()) {
			return Prefers.isFull() ? AspectRatioFrameLayout.RESIZE_MODE_FILL : AspectRatioFrameLayout.RESIZE_MODE_FIT;
		} else {
			return Prefers.isFull() ? IRenderView.AR_MATCH_PARENT : IRenderView.AR_ASPECT_FIT_PARENT;
		}
	}
}
