package com.jaka.recyclerviewapplication.model.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class ContactMigration4 extends Migration {

    public ContactMigration4() {
        super(3, 4);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("CREATE UNIQUE INDEX index_contact_remote_id ON contact(remote_id)");
    }
}
