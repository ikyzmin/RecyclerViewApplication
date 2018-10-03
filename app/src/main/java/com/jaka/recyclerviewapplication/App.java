package com.jaka.recyclerviewapplication;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.evernote.android.job.JobManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaka.recyclerviewapplication.jobs.NotificationJob;
import com.jaka.recyclerviewapplication.jobs.ScheduleJobCreator;
import com.jaka.recyclerviewapplication.model.ContactDatabase;
import com.jaka.recyclerviewapplication.model.migrations.ContactMigration2;
import com.jaka.recyclerviewapplication.model.migrations.ContactMigration3;
import com.jaka.recyclerviewapplication.model.migrations.ContactMigration4;

import androidx.room.Room;

public class App extends Application implements Application.ActivityLifecycleCallbacks {

    ContactDatabase contactDatabase;

    private static App instance;
    private int activityCount = 0;

    public static App getInstance() {
        return instance;
    }

    FirebaseFirestore firebaseFirestore;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        JobManager.create(this).addJobCreator(new ScheduleJobCreator());
        FirebaseApp.initializeApp(this);
        NotificationJob.scheduleMe();
        firebaseFirestore = FirebaseFirestore.getInstance();
        contactDatabase = Room.databaseBuilder(this, ContactDatabase.class, "contact")
                .addMigrations(new ContactMigration2())
                .addMigrations(new ContactMigration3())
                .addMigrations(new ContactMigration4())
                .build();
        registerActivityLifecycleCallbacks(this);
    }

    public ContactDatabase getDatabase() {
        return contactDatabase;
    }

    public CollectionReference getFirebaseCollection() {
        return firebaseFirestore.collection("contacts");
    }

    public boolean isAppIsInBackground() {
        return activityCount == 0;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activityCount++;
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activityCount--;
    }


}
