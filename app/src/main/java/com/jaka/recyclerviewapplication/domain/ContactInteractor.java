package com.jaka.recyclerviewapplication.domain;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.jaka.recyclerviewapplication.App;
import com.jaka.recyclerviewapplication.data.repositories.ContactContentProvider;
import com.jaka.recyclerviewapplication.model.Contact;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ContactInteractor {

    private static final ContentResolver contentResolver = App.getInstance().getContentResolver();
    private static final Uri uri = ContactContentProvider.CONTENT_URI;

    public void saveContact(Contact contact) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactContentProvider.FIRSTNAME, contact.getFirstName());
        contentValues.put(ContactContentProvider.LASTNAME, contact.getLastName());
        contentValues.put(ContactContentProvider.PHONE_NUMBER, contact.getPhoneNumber());
        contentResolver.insert(uri, contentValues);
    }

    public void saveContacts(List<Contact> contacts) {
        for (Contact contact : contacts) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ContactContentProvider.PHONE_NUMBER, contact.getPhoneNumber());
            contentValues.put(ContactContentProvider.FIRSTNAME, contact.getFirstName());
            contentValues.put(ContactContentProvider.LASTNAME, contact.getLastName());
            contentResolver.insert(uri, contentValues);
        }
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            contacts.add(new Contact(cursor.getString(3), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return contacts;
    }
}
