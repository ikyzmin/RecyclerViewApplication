package com.jaka.recyclerviewapplication.view;

import android.view.View;
import android.widget.TextView;

import com.jaka.recyclerviewapplication.R;
import com.jaka.recyclerviewapplication.model.Contact;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class ContactViewHolder extends RecyclerView.ViewHolder {

    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView phoneNumberTextView;

    ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        firstNameTextView = itemView.findViewById(R.id.first_name_text_view);
        lastNameTextView = itemView.findViewById(R.id.last_name_text_view);
        phoneNumberTextView = itemView.findViewById(R.id.phone_number_text_view);
    }

    void bind(Contact contact) {
        firstNameTextView.setText(contact.getFirstName());
        lastNameTextView.setText(contact.getLastName());
        phoneNumberTextView.setText(contact.getPhoneNumber());
    }

}
