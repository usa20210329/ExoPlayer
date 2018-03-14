package com.fongmi.android.tv;

import com.fongmi.android.library.ltv.Ltv;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncCallback;
import com.fongmi.android.tv.network.BaseApiService;
import com.fongmi.android.tv.utils.Prefers;

import static com.fongmi.android.library.ltv.Constant.*;

public class ApiService extends BaseApiService {

    @Override
    public void onInit(AsyncCallback callback) {
        new WebService(LTV_NOTICE).executeOnExecutor(mExecutor);
        new WebService(LTV_GEO, callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannels(AsyncCallback callback) {
        Ltv.getInstance().setId(Prefers.getMail());
        new WebService(LTV_CHANNEL, callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void getChannelUrl(Channel channel, AsyncCallback callback) {
        new WebService(LTV_CHANNEL_URL, channel.getNumber(), callback).executeOnExecutor(mExecutor);
    }

    @Override
    public void onRetry(AsyncCallback callback) {
        new WebService(LTV_GEO, callback).executeOnExecutor(mExecutor);
    }
}
