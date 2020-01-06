package com.fongmi.android.ltv.utils;

import android.app.Activity;
import android.content.Context;
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

class FileUtil {

	private static final String TAG = FileUtil.class.getSimpleName();

	private static String getApkName() {
		return App.getName().concat(".apk");
	}

	private static File getCachePath() {
		return App.getInstance().getExternalFilesDir("cache");
	}

	private static File getApkFile() {
		return new File(getCachePath(), getApkName());
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

	private static Uri getShareUri(Context context, File file) {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? Uri.fromFile(file) : FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".FileProvider", file);
	}

	static void checkUpdate(Activity activity, long version) {
		if (version > BuildConfig.VERSION_CODE) {
			Notify.show(R.string.app_update);
			startDownload(activity);
		} else {
			FileUtil.clearFile(getApkFile());
		}
	}

	private static void startDownload(Activity activity) {
		FirebaseStorage.getInstance().getReference().child(getApkName()).getFile(getApkFile()).addOnSuccessListener((FileDownloadTask.TaskSnapshot taskSnapshot) -> openFile(activity, getApkFile()));
	}

	private static void openFile(Activity activity, File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setDataAndType(getShareUri(activity, file), FileUtil.getMimeType(file.getName()));
		activity.startActivity(intent);
		activity.finish();
	}
}
