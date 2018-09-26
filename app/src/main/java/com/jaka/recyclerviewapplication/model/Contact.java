package com.jaka.recyclerviewapplication.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private int id;


    @ColumnInfo(name = "phone_number")
    private String phoneNumber;


    @ColumnInfo(name = "first_name")
    private String firstName;


    @ColumnInfo(name = "last_name")
    private String lastName;


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
