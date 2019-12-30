package com.fongmi.android.tv.model;

import com.fongmi.android.tv.AppDatabase;
import com.fongmi.android.tv.utils.Prefers;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class Type {

	private int id;
	private String name;
	private boolean hidden;
	private List<Channel> channel;

	public static Type create(DataSnapshot data) {
		return data.getValue(Type.class);
	}

	public static Type create() {
		return new Type(0, "收藏", AppDatabase.getInstance().getDao().getFavorite());
	}

	public Type() {
	}

	private Type(int id, String name, List<Channel> channel) {
		this.id = id;
		this.name = name;
		this.channel = channel;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isHidden() {
		return hidden;
	}

	public List<Channel> getChannel() {
		return channel;
	}

	public int getTextSize() {
		return Prefers.getSize() * 2 + 16;
	}
}
