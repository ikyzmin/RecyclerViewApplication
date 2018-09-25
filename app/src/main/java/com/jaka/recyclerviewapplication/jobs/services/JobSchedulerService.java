package com.jaka.recyclerviewapplication.jobs.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;


public class JobSchedulerService extends JobService {


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("TEST", params.getJobId() + "");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
