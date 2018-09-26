package com.jaka.recyclerviewapplication.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM contact")
    List<Contact> getContacts();

    @Query("SELECT * FROM contact WHERE id = (:id)")
    Contact getContactById(int id);

    @Insert
    void insertAll(Contact... contacts);

    @Delete
    void delete(Contact contact);
}