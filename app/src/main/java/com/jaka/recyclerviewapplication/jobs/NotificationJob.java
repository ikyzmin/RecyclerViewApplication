package com.jaka.recyclerviewapplication.jobs;

import android.content.Intent;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.jaka.recyclerviewapplication.services.FirebaseNotificationService;

import androidx.annotation.NonNull;

public class NotificationJob extends Job {

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        getContext().startService(new Intent(getContext(), FirebaseNotificationService.class));
        return Result.SUCCESS;
    }

    public static void scheduleMe() {
        new JobRequest.Builder(ScheduleJobCreator.NOTIFICATION_TAG)
                .startNow()
                .build()
                .schedule();
    }

}
