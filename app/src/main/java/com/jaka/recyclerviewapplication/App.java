package com.jaka.recyclerviewapplication;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.jaka.recyclerviewapplication.model.ContactDatabase;
import com.jaka.recyclerviewapplication.model.migrations.ContactMigration2;
import com.jaka.recyclerviewapplication.model.migrations.ContactMigration3;

import androidx.room.Room;

public class App extends Application {

    ContactDatabase contactDatabase;

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    FirebaseFirestore firebaseFirestore;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseApp.initializeApp(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        contactDatabase = Room.databaseBuilder(this, ContactDatabase.class, "contact")
                .addMigrations(new ContactMigration2())
                .addMigrations(new ContactMigration3())
                .build();
    }

    public ContactDatabase getDatabase() {
        return contactDatabase;
    }

    public CollectionReference getFirebaseCollection() {
        return firebaseFirestore.collection("contacts");
    }

    public FirebaseFirestore getFirebaseFireStore() {
        return firebaseFirestore;
    }
}
