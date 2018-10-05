package com.jaka.recyclerviewapplication.view.adapter.contact;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaka.recyclerviewapplication.R;
import com.jaka.recyclerviewapplication.model.Contact;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView phoneNumberTextView;
    private View foreground;
    private View background;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        firstNameTextView = itemView.findViewById(R.id.first_name_text_view);
        lastNameTextView = itemView.findViewById(R.id.last_name_text_view);
        phoneNumberTextView = itemView.findViewById(R.id.phone_number_text_view);
        foreground = itemView.findViewById(R.id.foreground_layout);
        background =itemView.findViewById(R.id.backgroud_layout);
    }

    public void bind(Contact contact, View.OnClickListener listener) {
        firstNameTextView.setText(contact.getFirstName());
        lastNameTextView.setText(contact.getLastName());
        phoneNumberTextView.setText(contact.getPhoneNumber());
        itemView.setOnClickListener(listener);
    }

    public View getForeground() {
        return foreground;
    }

    public View getBackground() {
        return background;
    }
}
