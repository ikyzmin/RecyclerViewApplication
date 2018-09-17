package com.jaka.recyclerviewapplication.view.adapter.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaka.recyclerviewapplication.R;
import com.jaka.recyclerviewapplication.model.Contact;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private final List<Contact> contacts = new ArrayList<>();

    private View.OnClickListener clickListener;

    public ContactsAdapter(View.OnClickListener clickListener){
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.i_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.bind(contacts.get(position),clickListener);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts.clear();
        this.contacts.addAll(contacts);
        notifyDataSetChanged();
    }
}
