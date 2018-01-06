package com.fongmi.android.tv.model;

import com.google.gson.annotations.SerializedName;

public class Account {

    @SerializedName("account")
    private String account;

    public String getAccount() {
        return account;
    }
}
