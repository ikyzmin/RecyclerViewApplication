package com.jaka.recyclerviewapplication.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.jaka.recyclerviewapplication.notifications.ScheduleNotificationManager;
import com.jaka.recyclerviewapplication.view.ScheduleFragment;

import androidx.annotation.NonNull;

public class ScheduleJob extends Job {

    private static final String MESSAGE_KEY = "message";
    private static final String TITLE_KEY = "title";
    private static final String DESCRIPTION_KEY = "description";


    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        String message = params.getExtras().getString(MESSAGE_KEY, "");
        String title = params.getExtras().getString(TITLE_KEY, "");
        String description = params.getExtras().getString(DESCRIPTION_KEY, "");
        ScheduleNotificationManager.showNotification(getContext(), 1, title,
                message, description);
        return Result.SUCCESS;
    }

    public static void scheduleMe(long time, String title) {
        scheduleMe(time, title, null);
    }

    public static void scheduleMe(long time, String title, String message) {
        scheduleMe(time, title, message, null);
    }

    public static void scheduleMe(long time, String title, String message, String description) {
        PersistableBundleCompat persistableBundleCompat = new PersistableBundleCompat();
        persistableBundleCompat.putString(MESSAGE_KEY, message);
        persistableBundleCompat.putString(TITLE_KEY, title);
        persistableBundleCompat.putString(DESCRIPTION_KEY, description);
        new JobRequest.Builder(ScheduleJobCreator.SCHEDULE_TAG)
                .setExact(time)
                .addExtras(persistableBundleCompat)
                .build()
                .schedule();

    }
}
