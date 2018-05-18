package com.example.yoonc.dmyphoto;

import android.app.Application;

import com.bumptech.glide.Glide;

/**
 * Created by yoonc on 2018-05-18.
 */

public class MyApplication extends Application {

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }
}
