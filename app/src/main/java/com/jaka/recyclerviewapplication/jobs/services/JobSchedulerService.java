package com.jaka.recyclerviewapplication.jobs.services;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.jaka.recyclerviewapplication.notifications.ScheduleNotificationManager;
import com.jaka.recyclerviewapplication.view.ScheduleFragment;


public class JobSchedulerService extends JobService {


    @Override
    public boolean onStartJob(JobParameters params) {
        switch (params.getJobId()) {
            case 1:
                ScheduleNotificationManager.showNotification(getApplicationContext(), 1, "Start your work",
                        "Time is now", params.getExtras().getString(ScheduleFragment.DESCRIPTION_EXTRA));
                break;
            case 2:
                ScheduleNotificationManager.showNotification(getApplicationContext(), 1,   "End Your Work",
                        "Time to relax ", params.getExtras().getString(ScheduleFragment.DESCRIPTION_EXTRA));
                break;
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
