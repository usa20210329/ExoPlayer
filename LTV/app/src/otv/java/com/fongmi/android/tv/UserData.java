package com.fongmi.android.tv;

import com.fongmi.android.tv.model.Account;
import com.fongmi.android.tv.model.Info;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
import com.fongmi.android.tv.utils.Encrypt;
import com.fongmi.android.tv.utils.HashId;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import static com.fongmi.android.tv.Constant.PASS;
import static com.fongmi.android.tv.Constant.UUID;

public class UserData {

    private List<Account> mItems;
    private Info mInfo;
    private String hx;
    private long ts;
    private int index;

    private static class Loader {
        static volatile UserData INSTANCE = new UserData();
    }

    public static UserData getInstance() {
        return Loader.INSTANCE;
    }

    private UserData() {
        mItems = new ArrayList<>();
    }

    void setInfo(Info info) {
        this.mInfo = info;
    }

    void setTsHx() {
        if (mItems.size() > 0) {
            mItems.remove(index);
            setAccount();
            return;
        }
        FirebaseDatabase.getInstance().getReference().child("account").addValueEventListener(new AsyncTaskRunnerCallback() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) mItems.add(iterator.next().getValue(Account.class));
                setAccount();
            }
        });
    }

    private void setAccount() {
        setIndex();
        setTs(mInfo.getTs());
        setHx(Encrypt.aes(mInfo.getIv(), mInfo.getKey(), getUser(mInfo)));
    }

    private void setIndex() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        index = (hour * 60 + min) / 10;
    }

    private String getUser(Info info) {
        String hash = new HashId(info.getSalt()).encode(info.getTs());
        return info.getIp() + "\t" + mItems.get(index).getAccount() + "\t" + PASS + "\t" + hash + "\t" + getUuidSum();
    }

    private int getUuidSum() {
        int sum = 0;
        for (char c : UUID.toCharArray()) sum += c;
        return sum;
    }

    private void setTs(long ts) {
        this.ts = ts;
    }

    private void setHx(String hx) {
        this.hx = hx;
    }

    public String getParam() {
        return "?ts=" + ts + "&hx=" + hx;
    }
}
