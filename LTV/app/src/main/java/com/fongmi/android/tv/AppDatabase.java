package com.fongmi.android.tv;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fongmi.android.tv.dao.ChannelDao;
import com.fongmi.android.tv.model.Channel;

@Database(entities = {Channel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

	private static volatile AppDatabase instance;

	public static synchronized AppDatabase getInstance() {
		if (instance == null) instance = create(App.getInstance());
		return instance;
	}

	private static AppDatabase create(Context context) {
		return Room.databaseBuilder(context, AppDatabase.class, BuildConfig.FLAVOR).allowMainThreadQueries().build();
	}

	public abstract ChannelDao getDao();
}
