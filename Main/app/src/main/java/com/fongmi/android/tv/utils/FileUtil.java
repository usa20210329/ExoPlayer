package com.fongmi.android.tv.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.BuildConfig;
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.impl.AsyncCallback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;

public class FileUtil {

	private static final String TAG = FileUtil.class.getSimpleName();

	public static File getCachePath() {
		return App.get().getExternalCacheDir();
	}

	public static File getCacheFile(String fileName) {
		return new File(getCachePath(), fileName);
	}

	public static void clearDir(File dir) {
		if (dir == null) return;
		if (dir.isDirectory()) for (File file : dir.listFiles()) clearDir(file);
		if (dir.delete()) Log.d(TAG, dir.getPath() + " File Deleted");
	}

	private static String getMimeType(String fileName) {
		String mimeType = URLConnection.guessContentTypeFromName(fileName);
		return TextUtils.isEmpty(mimeType) ? "*/*" : mimeType;
	}

	private static Uri getShareUri(File file) {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? Uri.fromFile(file) : FileProvider.getUriForFile(App.get(), App.get().getPackageName() + ".provider", file);
	}

	public static void checkUpdate(long version) {
		if (version > BuildConfig.VERSION_CODE) {
			Notify.show(R.string.app_update);
			download();
		} else {
			clearDir(getCachePath());
		}
	}

	private static void download() {
		new OkHttpClient().newCall(new Request.Builder().url(Token.getApk()).build()).enqueue(new AsyncCallback() {
			@Override
			public void onResponse(Response response) throws IOException {
				if (!response.isSuccessful()) return;
				File file = getCacheFile(response.header("Content-Disposition").split("=")[1]);
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(response.body().bytes());
				fos.close();
				open(file);
			}
		});
	}

	private static void open(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setDataAndType(getShareUri(file), FileUtil.getMimeType(file.getName()));
		App.get().startActivity(intent);
	}
}
