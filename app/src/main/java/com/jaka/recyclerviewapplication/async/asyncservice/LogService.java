package com.jaka.recyclerviewapplication.async.asyncservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LogService extends IntentService {

    public static void startLogService(Context context) {
        context.startService(new Intent(context, LogService.class));
    }

    public LogService() {
        super("LOG");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(50);
                Log.d(LogService.class.getSimpleName(), "progress = " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
