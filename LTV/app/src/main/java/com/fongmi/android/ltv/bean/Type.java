package com.fongmi.android.ltv.bean;

import com.fongmi.android.ltv.AppDatabase;
import com.fongmi.android.ltv.R;
import com.fongmi.android.ltv.utils.Utils;

import java.util.List;

public class Type extends Bean {

	private int id;
	private boolean hidden;
	private List<Channel> channel;

	public static Type create(int id) {
		Type type = new Type();
		type.setId(id);
		type.setName(Utils.getString(id));
		type.setLogo(type.isKeep() ? "keep.png" : "setting.png");
		return type;
	}

	public Type() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isHidden() {
		return hidden;
	}

	public List<Channel> getChannel() {
		return isKeep() ? AppDatabase.get().getDao().getKeep() : channel;
	}

	public void setChannel(List<Channel> channel) {
		this.channel = channel;
	}

	public boolean isKeep() {
		return getId() == R.string.channel_type_keep;
	}

	public boolean isSetting() {
		return getId() == R.string.channel_type_setting;
	}

	public int find(String number) {
		return getChannel().lastIndexOf(Channel.create(number));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Type)) return false;
		Type it = (Type) obj;
		return getName().equals(it.getName());
	}
}
