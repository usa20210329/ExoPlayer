package com.fongmi.android.ltv.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.fongmi.android.ltv.App;
import com.fongmi.android.ltv.BuildConfig;
import com.fongmi.android.ltv.R;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.net.URLConnection;

public class FileUtil {

	private static final String TAG = FileUtil.class.getSimpleName();

	private static String getApkName() {
		return App.getName().concat(".apk");
	}

	private static File getCachePath() {
		return App.get().getExternalCacheDir();
	}

	private static File getApkFile() {
		return getCacheFile(getApkName());
	}

	public static File getCacheFile(String fileName) {
		return new File(getCachePath(), fileName);
	}

	public static boolean isFile(String path) {
		return path.contains(App.get().getPackageName());
	}

	private static void clearFile(File file) {
		if (file.delete() && BuildConfig.DEBUG) {
			Log.d(TAG, file.getPath() + " File Deleted");
		}
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
			startDownload();
		} else {
			FileUtil.clearFile(getApkFile());
		}
	}

	private static void startDownload() {
		FirebaseStorage.getInstance().getReference().child(getApkName()).getFile(getApkFile()).addOnSuccessListener((FileDownloadTask.TaskSnapshot taskSnapshot) -> openFile(getApkFile()));
	}

	private static void openFile(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setDataAndType(getShareUri(file), FileUtil.getMimeType(file.getName()));
		App.get().startActivity(intent);
	}
}
