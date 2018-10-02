package com.jaka.recyclerviewapplication.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ScheduleJobCreator implements JobCreator {

    static final String SCHEDULE_TAG = "scheduleTag";

    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SCHEDULE_TAG:
                return new ScheduleJob();
            default:
                return null;
        }
    }
}
