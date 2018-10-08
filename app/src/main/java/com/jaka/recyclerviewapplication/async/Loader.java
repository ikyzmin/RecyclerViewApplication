package com.jaka.recyclerviewapplication.async;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.jaka.recyclerviewapplication.model.Contact;
import com.jaka.recyclerviewapplication.model.ContactDao;

import java.util.List;

public class Loader extends Handler {

    public static final int LOADING_PROCESS = 0;
    public static final int LOADING_START = 1;
    public static final int LOADING_COMPLETE = 2;

    public static final int GET_ALL_CONTACTS = 3;
    public static final int INSERT_CONTACT = 4;
    public static final int INSERT_CONTACTS = 5;
    public static final int DELETE_CONTACT = 6;

    Handler callbackHandler;
    private ContactDao contactDao;

    public Loader(Looper looper, ContactDao contactDao, Handler callbackHandler) {
        super(looper);
        this.callbackHandler = callbackHandler;
        this.contactDao = contactDao;
    }

    public Loader(Looper looper, Callback callback) {
        super(looper, callback);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case LOADING_START:
                callbackHandler.obtainMessage(LOADING_START).sendToTarget();
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(50);
                        callbackHandler.obtainMessage(LOADING_PROCESS, i).sendToTarget();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                callbackHandler.obtainMessage(LOADING_COMPLETE).sendToTarget();
                break;
            case GET_ALL_CONTACTS:
                List<Contact> contactList = contactDao.getContacts();
                callbackHandler.obtainMessage(LOADING_COMPLETE, contactList).sendToTarget();
                break;
            case INSERT_CONTACT:
                contactDao.insertAll((Contact) msg.obj);
                callbackHandler.obtainMessage(LOADING_COMPLETE).sendToTarget();
                break;
            case INSERT_CONTACTS:
                if (msg.obj instanceof Contact[]){
                    contactDao.insertAll((Contact[]) msg.obj);
                }else if (msg.obj instanceof List){
                    contactDao.insertAll((List<Contact>) msg.obj);
                }
                callbackHandler.obtainMessage(LOADING_COMPLETE).sendToTarget();
            case DELETE_CONTACT:
                if (msg.obj instanceof Contact){
                    contactDao.delete((Contact) msg.obj);
                }
                callbackHandler.obtainMessage(LOADING_COMPLETE).sendToTarget();
                break;
        }
    }
}
