package com.fongmi.android.library.otv.model;

import com.fongmi.android.library.otv.utils.Encrypt;
import com.fongmi.android.library.otv.utils.HashId;

import java.util.Calendar;
import java.util.List;

import static com.fongmi.android.library.otv.Constant.*;

public class User {

    private List<String> items;
    private Info info;
    private String hx;
    private int index;
    private long ts;

    public void setItems(List<String> items) {
        this.items = items;
        this.setAccount();
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    private void setHx(String hx) {
        this.hx = hx;
    }

    private void setTs(long ts) {
        this.ts = ts;
    }

    public String getHx() {
        return hx;
    }

    public long getTs() {
        return ts;
    }

    public void change() {
        if (items == null || items.isEmpty()) return;
        items.remove(index);
        setAccount();
    }

    private void setAccount() {
        setIndex();
        setTs(info.getTs());
        setHx(Encrypt.aes(info.getIv(), info.getKey(), getUser(info)));
    }

    private void setIndex() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        index = (hour * 60 + min) / 30;
    }

    private String getUser(Info info) {
        int sum = 0;
        for (char c : UUID.toCharArray()) sum += c;
        String hash = new HashId(info.getSalt()).encode(info.getTs());
        return info.getIp() + "\t" + items.get(index) + "\t" + PASS + "\t" + hash + "\t" + sum;
    }
}
