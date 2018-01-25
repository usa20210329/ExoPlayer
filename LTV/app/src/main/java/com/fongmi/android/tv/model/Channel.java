package com.fongmi.android.tv.model;

import android.text.TextUtils;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.utils.Prefers;
import com.fongmi.android.tv.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Channel {

    @SerializedName(value = "number", alternate = {"CH_NO", "channel"})
    private int number;
    @SerializedName(value = "name", alternate = {"CH_NAME"})
    private String name;
    @SerializedName(value = "url", alternate = {"CH_URL"})
    private String url;

    private transient boolean hidden;
    private transient boolean select;
    private transient String realUrl;

    public Channel() {
    }

    public Channel(String number) {
        this.number = Integer.valueOf(number);
    }

    public static Channel create(String number) {
        return new Channel(number);
    }

    public static Channel create(DataSnapshot data) {
        return data.getValue(Channel.class);
    }

    public static List<Channel> arrayFrom(String str) {
        if (TextUtils.isEmpty(str)) {
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(str, new TypeToken<ArrayList<Channel>>() {}.getType());
        }
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? "" : name;
    }

    public String getUrl() {
        return TextUtils.isEmpty(url) ? "" : url;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isSelect() {
        return select;
    }

    private void setSelect(boolean select) {
        this.select = select;
    }

    public String getRealUrl() {
        return TextUtils.isEmpty(realUrl) ? "" : realUrl;
    }

    public void setRealUrl(String realUrl) {
        this.realUrl = realUrl;
    }

    public String getFile() {
        return getUrl().substring(getUrl().lastIndexOf("/") + 1, getUrl().length());
    }

    public void deselect() {
        setSelect(false);
    }

    public void select() {
        setSelect(true);
    }

    public boolean hasUrl() {
        return getRealUrl().length() > 0;
    }

    public String getInfo() {
        return Utils.getString(R.string.channel_info, getNumber(), getName());
    }

    public int getTextSize() {
        return Prefers.getSize() * 2 + 14;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Channel)) return false;
        Channel it = (Channel) obj;
        return number == it.number;
    }
}
