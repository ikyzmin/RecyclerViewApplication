package com.jaka.recyclerviewapplication.async;

import android.os.Handler;
import android.os.HandlerThread;


public class LoaderThread extends HandlerThread {

    private static final String LOADER = "Loader";

    Loader loader;

    public LoaderThread() {
        super(LOADER);
    }

    public void initHandler(Handler callbackHandler) {
        loader = new Loader(getLooper(), callbackHandler);
    }

    public void startLoading() {
        loader.obtainMessage(Loader.LOADING_START).sendToTarget();
    }

}
