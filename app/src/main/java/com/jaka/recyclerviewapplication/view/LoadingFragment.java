package com.jaka.recyclerviewapplication.view;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jaka.recyclerviewapplication.R;
import com.jaka.recyclerviewapplication.async.Loader;
import com.jaka.recyclerviewapplication.async.DatabaseThread;
import com.jaka.recyclerviewapplication.async.asyncservice.LoaderService;
import com.jaka.recyclerviewapplication.async.asynctask.LoaderAsyncTask;
import com.jaka.recyclerviewapplication.jobs.services.JobSchedulerService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LoadingFragment extends Fragment {

    private ProgressBar progressBar;
    private Button loadButton;
    private Button loadButtonAsyncTask;
    private Button loadButtonIntentService;
    private Button loadButtonWithJob;
    private DatabaseThread loaderThread;
    private TextView loadingProgressTextView;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LoaderService.LOADING_START)) {
                progressBar.setVisibility(View.VISIBLE);
                loadButtonIntentService.setText(R.string.loading);
                loadButtonAsyncTask.setEnabled(false);
                loadButton.setEnabled(false);
                loadingProgressTextView.setVisibility(View.VISIBLE);
                loadingProgressTextView.setText(getString(R.string.loading_progress, 0 + ""));
            } else if (intent.getAction().equals(LoaderService.LOADING_PROGRESS)) {
                progressBar.setProgress(intent.getExtras().getInt(LoaderService.PROGRESS_EXTRA, 0));
                loadingProgressTextView.setText(getString(R.string.loading_progress, intent.getExtras().getInt(LoaderService.PROGRESS_EXTRA, 0) + ""));
            } else if (intent.getAction().equals(LoaderService.LOADING_END)) {
                loadButtonAsyncTask.setEnabled(true);
                loadButton.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                loadingProgressTextView.setVisibility(View.INVISIBLE);
                loadButtonIntentService.setText(R.string.load);
                progressBar.setProgress(0);
            }
        }
    };

    private Handler callbackHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Loader.LOADING_START:
                    progressBar.setVisibility(View.VISIBLE);
                    loadButton.setText(R.string.loading);
                    loadButtonAsyncTask.setEnabled(false);
                    loadingProgressTextView.setVisibility(View.VISIBLE);
                    loadingProgressTextView.setText(getString(R.string.loading_progress, 0 + ""));
                    break;
                case Loader.LOADING_PROCESS:
                    progressBar.setProgress((Integer) msg.obj);
                    loadingProgressTextView.setText(getString(R.string.loading_progress, msg.obj + ""));
                    break;
                case Loader.LOADING_COMPLETE:
                    loadButtonAsyncTask.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    loadingProgressTextView.setVisibility(View.INVISIBLE);
                    loadButton.setText(R.string.load);
                    progressBar.setProgress(0);
            }
        }
    };

    public LoadingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_loading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.load_progress);
        loadButton = view.findViewById(R.id.load_button);
        loadingProgressTextView = view.findViewById(R.id.loading_progress);
        loadButtonAsyncTask = view.findViewById(R.id.load_button_async_task);
        loadButtonIntentService = view.findViewById(R.id.load_button_intent_service);
        loadButtonWithJob = view.findViewById(R.id.load_button_job_scheduler);
        loadButtonWithJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName serviceName = new ComponentName(v.getContext(), JobSchedulerService.class);
                JobInfo jobInfo = new JobInfo.Builder(1, serviceName)
                        .setPeriodic(5*1000*60)
                        .build();
                JobScheduler scheduler = (JobScheduler) v.getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (scheduler!=null) {
                    int result = scheduler.schedule(jobInfo);
                    if (result == JobScheduler.RESULT_SUCCESS) {
                        Log.d("TEST", "Job scheduled successfully!");
                    }
                }
            }
        });
        loadButtonIntentService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startService(new Intent(getContext(), LoaderService.class));
            }
        });
        loadButtonAsyncTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoaderAsyncTask loaderAsyncTask = new LoaderAsyncTask(new LoaderAsyncTask.LoaderCallback() {
                    @Override
                    public void onProgressChanged(int progress) {
                        loadingProgressTextView.setText(getString(R.string.loading_progress, progress + ""));
                        progressBar.setProgress(progress);
                    }

                    @Override
                    public void onLoadComplete() {
                        loadButton.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        loadingProgressTextView.setVisibility(View.INVISIBLE);
                        loadButtonAsyncTask.setText(R.string.load_async_task);
                        progressBar.setProgress(0);
                    }

                    @Override
                    public void onLoadingStart() {
                        loadButton.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        loadButtonAsyncTask.setText(R.string.loading);
                        loadingProgressTextView.setVisibility(View.VISIBLE);
                    }
                });
                loaderAsyncTask.execute();
            }
        });
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loaderThread.startLoading();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderThread = new DatabaseThread();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(LoaderService.LOADING_START);
        intentFilter.addAction(LoaderService.LOADING_PROGRESS);
        intentFilter.addAction(LoaderService.LOADING_END);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }
}
