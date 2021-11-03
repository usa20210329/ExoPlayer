package com.fongmi.android.ltv.bean;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.room.Ignore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.fongmi.android.ltv.App;
import com.fongmi.android.ltv.utils.Utils;

public class Bean {

	@Ignore
	private boolean select;
	private String name;
	private String logo;

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public String getName() {
		return TextUtils.isEmpty(name) ? "" : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public void loadLogo(ImageView view) {
		Glide.with(App.get()).load(Utils.getImageUrl(getLogo())).transition(DrawableTransitionOptions.withCrossFade()).into(view);
	}
}
