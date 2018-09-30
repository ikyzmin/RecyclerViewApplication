package com.jaka.recyclerviewapplication.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class}, version = 3, exportSchema = false)
public abstract class ContactDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();
}
