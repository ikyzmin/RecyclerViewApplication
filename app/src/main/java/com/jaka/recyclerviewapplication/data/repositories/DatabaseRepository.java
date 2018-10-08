package com.jaka.recyclerviewapplication.data.repositories;

import android.os.Handler;

import com.jaka.recyclerviewapplication.async.DatabaseThread;
import com.jaka.recyclerviewapplication.model.Contact;
import com.jaka.recyclerviewapplication.model.ContactDatabase;

import java.util.Collections;
import java.util.List;

public class DatabaseRepository {

    private DatabaseThread databaseThread;
    private ContactDatabase contactDatabase;

    public DatabaseRepository(ContactDatabase database) {
        this.contactDatabase = database;
        databaseThread = new DatabaseThread();
    }

    public void quit() {
        if (databaseThread != null) {
            databaseThread.quit();
        }
    }

    public void start(Handler callback) {
        if (databaseThread == null || !databaseThread.isAlive()) databaseThread = new DatabaseThread();
        databaseThread.start();
        databaseThread.initHandler(callback, contactDatabase.contactDao());
    }


    public void getAllContacts() {
        databaseThread.getAllContacts();
    }

    public void insertContact(Contact contact) {
        databaseThread.insertContact(contact);
    }

    public void removeContact(Contact contact) {
        databaseThread.removeContact(contact);
    }

    public void insertContacts(Contact... contact) {
        databaseThread.insertContact(contact);
    }

    public void insertContacts(List<Contact> contacts) {
        databaseThread.insertContact(contacts);
    }

}
