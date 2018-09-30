package com.jaka.recyclerviewapplication.model.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class ContactMigration3 extends Migration {

    public ContactMigration3() {
        super(2, 3);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE contact ADD COLUMN remote_id TEXT");
    }
}
