package com.jaka.recyclerviewapplication.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.jaka.recyclerviewapplication.model.converters.DateTimeConverters;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(indices = {@Index(value = {"remote_id"}, unique = true)})
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "remote_id")
    private String remoteId;


    @ColumnInfo(name = "phone_number")
    private String phoneNumber;


    @ColumnInfo(name = "first_name")
    private String firstName;


    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "date")
    @TypeConverters({DateTimeConverters.class})
    public Date date = new Date();

    public Contact() {

    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Exclude
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Exclude
    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public static class Builder {
        private Contact contact;

        public Builder() {
            contact = new Contact();
        }

        public Builder firstName(String firstName) {
            contact.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            contact.lastName = lastName;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            contact.phoneNumber = phoneNumber;
            return this;
        }

        public Contact build() {
            return contact;
        }

    }
}
