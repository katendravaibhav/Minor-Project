package com.example.client;

import android.app.Application;

public class ClientApplication extends Application {
    private static ClientApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ClientApplication getInstance() {
        return instance;
    }
}
