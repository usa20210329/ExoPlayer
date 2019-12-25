package com.fongmi.android.tv.model;

import com.fongmi.android.tv.utils.Prefers;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class Type {

	private int type;
	private String name;
	private boolean hidden;
	private List<Channel> channel;

	public static Type create(DataSnapshot data) {
		return data.getValue(Type.class);
	}

	public int getType() {
		return type;
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
