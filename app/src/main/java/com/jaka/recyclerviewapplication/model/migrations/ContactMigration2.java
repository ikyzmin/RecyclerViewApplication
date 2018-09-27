package com.jaka.recyclerviewapplication.model.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class ContactMigration2 extends Migration {

    public ContactMigration2() {
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE contact ADD COLUMN date TEXT");
    }
}
