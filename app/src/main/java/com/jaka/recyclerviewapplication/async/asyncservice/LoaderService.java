package com.jaka.recyclerviewapplication.async.asyncservice;

import android.app.IntentService;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LoaderService extends IntentService {

    public static final String LOADING_START = "LOADEING_START";
    public static final String LOADING_PROGRESS = "LOADEING_STARTED";
    public static final String LOADING_END = "LOADEING_END";

    public static final String PROGRESS_EXTRA = "PROGRESS";

    public LoaderService() {
        super("Loader");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(LOADING_START));

        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(50);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(LOADING_PROGRESS).putExtra(PROGRESS_EXTRA, i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(LOADING_END));
    }
}
