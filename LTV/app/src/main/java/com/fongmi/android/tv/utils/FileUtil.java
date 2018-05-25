package com.fongmi.android.tv.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.BuildConfig;
import com.fongmi.android.tv.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.net.URLConnection;

class FileUtil {

	private static final String TAG = FileUtil.class.getSimpleName();

	private static String getApkName() {
		return BuildConfig.FLAVOR.concat(".apk");
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
		try {
			String mimeType = URLConnection.guessContentTypeFromName(fileName);
			return TextUtils.isEmpty(mimeType) ? "*/*" : mimeType;
		} catch (Exception e) {
			return "*/*";
		}
	}

	private static Uri getProviderUri(Context context, File file) {
		return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
	}

	static void checkUpdate(Activity activity, long version) {
		if (version > BuildConfig.VERSION_CODE) {
			startDownload(activity);
		} else {
			FileUtil.clearFile(getApkFile());
		}
	}

	private static void startDownload(final Activity activity) {
		FirebaseStorage.getInstance().getReference().child(getApkName()).getFile(getApkFile()).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
				Notify.show(R.string.channel_update);
				openFile(activity);
			}
		});
	}

	private static void openFile(final Activity activity) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				FileUtil.openFile(activity, getApkFile());
			}
		}, 2000);
	}

	private static void openFile(Activity activity, File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? Uri.fromFile(file) : FileUtil.getProviderUri(activity, file);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setDataAndType(uri, FileUtil.getMimeType(file.getName()));
		try {
			activity.startActivity(intent);
			activity.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
