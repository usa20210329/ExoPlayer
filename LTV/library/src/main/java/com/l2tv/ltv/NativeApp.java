package com.l2tv.ltv;

public class NativeApp {

	public static void loadLibrary() {
		try {
			System.loadLibrary("native-lib");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static native String getinfo64(String str);

}
