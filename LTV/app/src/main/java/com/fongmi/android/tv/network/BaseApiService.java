package com.fongmi.android.tv.network;

import com.fongmi.android.tv.ApiService;
import com.fongmi.android.tv.impl.ApiServiceImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseApiService implements ApiServiceImpl {

    protected ExecutorService mExecutor = Executors.newCachedThreadPool();

    private static class Loader {
        static volatile ApiService INSTANCE = new ApiService();
    }

    public static ApiService getInstance() {
        return Loader.INSTANCE;
    }

    @Override
    public void onInit(AsyncCallback callback) {
        callback.onResponse(true);
    }
}
