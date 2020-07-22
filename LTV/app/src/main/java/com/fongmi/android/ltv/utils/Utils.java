package com.fongmi.android.ltv.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;

import com.fongmi.android.ltv.App;

public class Utils {

	private static DisplayMetrics getDisplayMetrics() {
		return App.get().getResources().getDisplayMetrics();
	}

	public static String getString(int resId) {
		return App.get().getString(resId);
	}

	public static int dp2px(int dpValue) {
		return Math.round(dpValue * getDisplayMetrics().density);
	}

	public static boolean isDigitKey(int keyCode) {
		return isNumber(keyCode) || isNumberPad(keyCode);
	}

	public static boolean hasEvent(KeyEvent event) {
		return isArrowKey(event) || isBackKey(event) || isMenuKey(event) || event.isLongPress();
	}

	private static boolean isArrowKey(KeyEvent event) {
		return isEnterKey(event) || isUpKey(event) || isDownKey(event) || isLeftKey(event) || isRightKey(event);
	}

	private static boolean isNumber(int keyCode) {
		return keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9;
	}

	static boolean isNumberPad(int keyCode) {
		return keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9;
	}

	static boolean isBackKey(KeyEvent event) {
		return event.getKeyCode() == KeyEvent.KEYCODE_BACK;
	}

	static boolean isMenuKey(KeyEvent event) {
		return event.getKeyCode() == KeyEvent.KEYCODE_MENU;
	}

	static boolean isEnterKey(KeyEvent event) {
		return event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_SPACE || event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER;
	}

	static boolean isUpKey(KeyEvent event) {
		return event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent.KEYCODE_CHANNEL_UP || event.getKeyCode() == KeyEvent.KEYCODE_PAGE_UP || event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS;
	}

	static boolean isDownKey(KeyEvent event) {
		return event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_CHANNEL_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_PAGE_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT;
	}

	static boolean isLeftKey(KeyEvent event) {
		return event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT;
	}

	static boolean isRightKey(KeyEvent event) {
		return event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT;
	}

	public static void showViews(View... views) {
		for (View view : views) showView(view);
	}

	public static void hideViews(View... views) {
		for (View view : views) hideView(view);
	}

	private static void showView(View view) {
		view.animate().alpha(1).setDuration(250).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				view.setVisibility(View.VISIBLE);
			}
		}).start();
	}

	private static void hideView(View view) {
		view.animate().alpha(0).setDuration(250).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				view.setVisibility(View.GONE);
			}
		}).start();
	}

	public static void setImmersiveMode(Activity activity) {
		int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		activity.getWindow().getDecorView().setSystemUiVisibility(flags);
	}

	static String getAndroidId() {
		return Settings.Secure.getString(App.get().getContentResolver(), Settings.Secure.ANDROID_ID);
	}

	static String getDevice() {
		String model = Build.MODEL;
		String manufacturer = Build.MANUFACTURER;
		if (model.startsWith(manufacturer)) return model;
		else return manufacturer + " " + model;
	}
}
