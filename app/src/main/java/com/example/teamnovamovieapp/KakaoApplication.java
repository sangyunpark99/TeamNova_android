package com.example.teamnovamovieapp;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this, "4040cd4d4dfe39dd31f91bd8b3a92cf0");
    }
}
