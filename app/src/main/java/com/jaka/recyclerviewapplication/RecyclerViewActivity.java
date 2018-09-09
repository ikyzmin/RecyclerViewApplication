package com.jaka.recyclerviewapplication;

import android.os.Bundle;

import com.jaka.recyclerviewapplication.model.Contact;
import com.jaka.recyclerviewapplication.view.ContactsAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewActivity extends AppCompatActivity {

    private final List<Contact> contactList = new ArrayList<>();

    public RecyclerViewActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_recycler);
        mockContacts();
        initRecyclerView();
    }

    private void mockContacts() {
        contactList.add(new Contact("+7999171139", "Ilia", "Kuzmin"));
        contactList.add(new Contact("+7178546734", "Petr", "Petrov"));
        contactList.add(new Contact("+7010101010", "Ivan", "Ivanov"));
        contactList.add(new Contact("+7123321123", "Vasylii", "Vasiliev"));
        contactList.add(new Contact("+7534123423", "Kirill", "Boshirov"));
        contactList.add(new Contact("+7145251252", "John", "Doe"));
        contactList.add(new Contact("+7634631362", "Brad", "Pitt"));
        contactList.add(new Contact("+7541525125", "Alexander", "Kuticyn"));
        contactList.add(new Contact("+7564634643", "Mark", "Twen"));
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ContactsAdapter contactsAdapter = new ContactsAdapter();
        contactsAdapter.setContacts(contactList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(contactsAdapter);
    }
}