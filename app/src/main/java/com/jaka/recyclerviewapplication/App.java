package com.jaka.recyclerviewapplication;

import android.app.Application;
import android.content.Context;

import com.jaka.recyclerviewapplication.model.ContactDatabase;

import androidx.room.Room;

public class App extends Application {

    ContactDatabase contactDatabase;

    private static App instance;

    public static App getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        contactDatabase = Room.databaseBuilder(this, ContactDatabase.class, "contact").build();
    }

    public ContactDatabase getDatabase() {
        return contactDatabase;
    }

}
