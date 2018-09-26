package com.jaka.recyclerviewapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaka.recyclerviewapplication.async.Loader;
import com.jaka.recyclerviewapplication.data.repositories.DatabaseRepository;
import com.jaka.recyclerviewapplication.domain.ContactInteractor;
import com.jaka.recyclerviewapplication.model.Contact;
import com.jaka.recyclerviewapplication.view.AddContactActivity;
import com.jaka.recyclerviewapplication.view.ScheduleFragment;
import com.jaka.recyclerviewapplication.view.adapter.contact.ContactsAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewActivity extends AppCompatActivity {

    private final List<Contact> contactList = new ArrayList<>();
    private final DatabaseRepository databaseRepository = new DatabaseRepository(App.getInstance().getDatabase(), new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Loader.LOADING_COMPLETE:
                    if (msg.obj != null) {
                        contactsAdapter.setContacts((List<Contact>) msg.obj);
                    }
                    break;
            }
        }
    });

    private static final ContactInteractor contactInteractor = new ContactInteractor();
    ContactsAdapter contactsAdapter = new ContactsAdapter(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new ScheduleFragment(), ScheduleFragment.class.getSimpleName())
                    .addToBackStack(null)
                    .commit();
        }
    });

    public RecyclerViewActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_recycler);
        mockContacts();
        initRecyclerView();
        FloatingActionButton floatingActionButton = findViewById(R.id.add_contact_floating_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecyclerViewActivity.this, AddContactActivity.class));
            }
        });
    }

    private void mockContacts() {
        // contactInteractor.saveContacts(contactList);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        //contactsAdapter.setContacts(contactInteractor.getAllContacts());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(contactsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseRepository.start();
        databaseRepository.getAllContacts();
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseRepository.quit();
    }
}
