package com.fongmi.android.ltv;

import android.app.Application;
import android.content.Context;

import com.fongmi.android.ltv.utils.Utils;
import com.tvbus.engine.PmsHook;

import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class App extends Application {

	private static App instance;
	private OkHttpClient client;

	public App() {
		instance = this;
	}

	public static App get() {
		return instance;
	}

	public static String getName() {
		return Utils.getString(R.string.app_id).toLowerCase();
	}

	public OkHttpClient getClient() {
		return client;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		PmsHook.inject(base);
		initOkHttp();
	}

	private void initOkHttp() {
		try {
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, trustAllCerts, new SecureRandom());
			client = new OkHttpClient().newBuilder().proxy(Proxy.NO_PROXY).sslSocketFactory(context.getSocketFactory(), (X509TrustManager) trustAllCerts[0]).hostnameVerifier((hostname, session) -> true).connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) { }

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) { }

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			}
	};
}