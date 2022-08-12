package com.forcetech.android;

public class ForceTV {

	public static int P2P = 9906;
	public static int P5P = 9909;

	public void start(String lib, int port) {
		try {
			System.loadLibrary(lib);
			start(port, 20971520);
		} catch (Throwable ignored) {
		}
	}

	public static int getPort(String url) {
		if (url.toLowerCase().startsWith("p2p://")) return P2P;
		else if (url.toLowerCase().startsWith("p5p://")) return P5P;
		return -1;
	}

	public native int start(int port, int size);

	public native int stop();
}
