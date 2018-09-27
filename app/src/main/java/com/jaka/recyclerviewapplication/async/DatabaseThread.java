package com.jaka.recyclerviewapplication.async;

import android.os.Handler;
import android.os.HandlerThread;

import com.jaka.recyclerviewapplication.model.Contact;
import com.jaka.recyclerviewapplication.model.ContactDao;


public class DatabaseThread extends HandlerThread {

    private static final String LOADER = "Loader";

    private Loader loader;

    public DatabaseThread() {
        super(LOADER);
    }

    public void initHandler(Handler callbackHandler, ContactDao contactDao) {
        if (loader != null) {
            loader.removeMessages(Loader.GET_ALL_CONTACTS);
            loader.removeMessages(Loader.INSERT_CONTACT);
        }
        loader = new Loader(getLooper(), contactDao, callbackHandler);
    }

    public void startLoading() {
        loader.obtainMessage(Loader.LOADING_START).sendToTarget();
    }

    public void getAllContacts() {
        loader.obtainMessage(Loader.GET_ALL_CONTACTS).sendToTarget();
    }

    public void insertContact(Contact contact) {
        loader.obtainMessage(Loader.INSERT_CONTACT, contact).sendToTarget();
    }

}
