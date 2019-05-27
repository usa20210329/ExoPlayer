package com.fongmi.android.tv;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.devbrackets.android.exomedia.ExoMedia;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;

import okhttp3.OkHttpClient;

public class App extends Application {

	private static App instance;

	public App() {
		instance = this;
	}

	public static App getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		configureExoMedia();
	}

	private void configureExoMedia() {
		ExoMedia.setDataSourceFactoryProvider((@NonNull String userAgent, @Nullable TransferListener listener) -> new OkHttpDataSourceFactory(new OkHttpClient(), "VasCreativePlayer/20.19.0520 (Linux;Android 5.1.1) ExoPlayerLib/2.0.0", listener));
	}
}
