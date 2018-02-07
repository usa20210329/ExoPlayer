package com.fongmi.android.library.otv;

import com.fongmi.android.library.otv.model.Info;
import com.fongmi.android.library.otv.model.User;
import com.fongmi.android.library.otv.utils.Utils;

import static com.fongmi.android.library.otv.Constant.*;

public class Otv {

    private User mUser;

    private static class Loader {
        static volatile Otv INSTANCE = new Otv();
    }

    public static Otv getInstance() {
        return Loader.INSTANCE;
    }

    private Otv() {
        mUser = new User();
        Utils.getAccount(mUser);
    }

    public String getInfo() {
        mUser.setInfo(Info.objectFrom(Utils.getResult(INFO)));
        return null;
    }

    public String getChannel() {
        return Utils.getResult(CHANNEL);
    }

    public String getUrl(String url) {
        return Utils.getRealUrl(url, mUser);
    }

    public void onRetry() {
        mUser.change();
    }
}
