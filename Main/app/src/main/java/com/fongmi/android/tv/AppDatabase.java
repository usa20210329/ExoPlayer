package com.fongmi.android.tv;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fongmi.android.tv.bean.Channel;
import com.fongmi.android.tv.dao.ChannelDao;

@Database(entities = {Channel.class}, version = 13, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

	private static volatile AppDatabase instance;

	public static synchronized AppDatabase get() {
		if (instance == null) instance = create(App.get());
		return instance;
	}

	private static AppDatabase create(Context context) {
		return Room.databaseBuilder(context, AppDatabase.class, "bear").allowMainThreadQueries().fallbackToDestructiveMigration().build();
	}

	public abstract ChannelDao getDao();
}
