package com.jaka.recyclerviewapplication.data.repositories;

import android.os.Handler;

import com.jaka.recyclerviewapplication.async.DatabaseThread;
import com.jaka.recyclerviewapplication.model.Contact;
import com.jaka.recyclerviewapplication.model.ContactDatabase;

import java.util.Collections;
import java.util.List;

public class DatabaseRepository {

    private DatabaseThread databaseThread = new DatabaseThread();
    private Handler callback;
    private ContactDatabase contactDatabase;

    public DatabaseRepository(ContactDatabase database, Handler callbackHandler) {
        this.callback = callbackHandler;
        this.contactDatabase = database;
    }

    public void quit() {
        databaseThread.quit();
    }

    public void start() {
        databaseThread.start();
        databaseThread.initHandler(callback, contactDatabase.contactDao());
    }


    public void getAllContacts() {
        databaseThread.getAllContacts();
    }

    public void insertContact(Contact contact) {
        databaseThread.insertContact(contact);
    }

}
