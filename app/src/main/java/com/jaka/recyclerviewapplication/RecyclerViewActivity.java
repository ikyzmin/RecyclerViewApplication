package com.jaka.recyclerviewapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.jaka.recyclerviewapplication.async.Loader;
import com.jaka.recyclerviewapplication.data.repositories.DatabaseRepository;
import com.jaka.recyclerviewapplication.domain.ContactInteractor;
import com.jaka.recyclerviewapplication.model.Contact;
import com.jaka.recyclerviewapplication.view.AddContactActivity;
import com.jaka.recyclerviewapplication.view.ScheduleFragment;
import com.jaka.recyclerviewapplication.view.adapter.contact.ContactsAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewActivity extends AppCompatActivity {

    private final List<Contact> contactList = new ArrayList<>();
    private DatabaseRepository databaseRepository;

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

        databaseRepository = new DatabaseRepository(App.getInstance().getDatabase());
        App.getInstance().getFirebaseCollection().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Contact> contacts = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Contact contact = documentSnapshot.toObject(Contact.class);
                    contact.setRemoteId(documentSnapshot.getReference().getId());
                    contacts.add(contact);
                }
                databaseRepository.insertContacts(contacts);
                databaseRepository.getAllContacts();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplication(), "Batch fail", Toast.LENGTH_LONG).show();
                Log.e(RecyclerViewActivity.class.getSimpleName(), "Batch Failed", e);
            }
        });
        databaseRepository.start(new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Loader.LOADING_COMPLETE:
                        if (msg.obj != null) {
                            if (msg.obj instanceof List) {
                                final List<Contact> contacts = (List<Contact>) msg.obj;
                                contactsAdapter.setContacts(contacts);
                            }
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseRepository.quit();
    }
}
