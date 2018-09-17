package com.jaka.recyclerviewapplication.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jaka.recyclerviewapplication.R;
import com.jaka.recyclerviewapplication.async.Loader;
import com.jaka.recyclerviewapplication.async.LoaderThread;
import com.jaka.recyclerviewapplication.async.asynctask.LoaderAsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoadingFragment extends Fragment {

    private ProgressBar progressBar;
    private Button loadButton;
    private Button loadButtonAsyncTask;
    private LoaderThread loaderThread;
    private TextView loadingProgressTextView;

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
        loaderThread = new LoaderThread();
    }

    @Override
    public void onResume() {
        super.onResume();
        loaderThread.start();
        loaderThread.initHandler(callbackHandler);
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderThread.quit();
    }
}
