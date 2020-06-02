package com.fongmi.android.ltv;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.dao.ChannelDao;

@Database(entities = {Channel.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

	private static volatile AppDatabase instance;

	public static synchronized AppDatabase getInstance() {
		if (instance == null) instance = create(App.get());
		return instance;
	}

	private static AppDatabase create(Context context) {
		return Room.databaseBuilder(context, AppDatabase.class, App.getName()).allowMainThreadQueries().fallbackToDestructiveMigration().build();
	}

	public abstract ChannelDao getDao();
}
