package com.jaka.recyclerviewapplication.async.asynctask;

import android.os.AsyncTask;

public class LoaderAsyncTask extends AsyncTask<Void, Integer, Void> {

    public interface LoaderCallback {
        void onProgressChanged(int progress);

        void onLoadComplete();

        void onLoadingStart();
    }

    private LoaderCallback loaderCallback;

    public LoaderAsyncTask(LoaderCallback loaderCallback) {
        this.loaderCallback = loaderCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loaderCallback.onLoadingStart();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(50);
                publishProgress(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        loaderCallback.onLoadComplete();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        loaderCallback.onProgressChanged(values[0]);
    }
}
