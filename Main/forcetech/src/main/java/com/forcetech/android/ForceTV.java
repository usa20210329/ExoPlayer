package com.forcetech.android;

public class ForceTV {

	public void start(String libName, int port) {
		try {
			System.loadLibrary(libName);
			start(port, 20971520);
		} catch (Throwable ignored) {
		}
	}

	public native int start(int port, int mem);

	public native int stop();
}
