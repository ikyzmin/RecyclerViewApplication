package com.jaka.recyclerviewapplication.async;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class Loader extends Handler {

    public static final int LOADING_PROCESS = 0;
    public static final int LOADING_START = 1;
    public static final int LOADING_COMPLETE = 2;

    Handler callbackHandler;

    public Loader(Looper looper, Handler callbackHandler) {
        super(looper);
        this.callbackHandler = callbackHandler;
    }

    public Loader(Looper looper, Callback callback) {
        super(looper, callback);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case LOADING_START:
                callbackHandler.obtainMessage(LOADING_START).sendToTarget();
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(50);
                        callbackHandler.obtainMessage(LOADING_PROCESS, i).sendToTarget();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                callbackHandler.obtainMessage(LOADING_COMPLETE).sendToTarget();
                break;
        }
    }
}
