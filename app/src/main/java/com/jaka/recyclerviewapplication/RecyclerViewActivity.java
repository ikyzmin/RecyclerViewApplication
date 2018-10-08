package com.jaka.recyclerviewapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaka.recyclerviewapplication.async.Loader;
import com.jaka.recyclerviewapplication.data.repositories.DatabaseRepository;
import com.jaka.recyclerviewapplication.domain.ContactInteractor;
import com.jaka.recyclerviewapplication.model.Contact;
import com.jaka.recyclerviewapplication.view.AddContactActivity;
import com.jaka.recyclerviewapplication.view.ScheduleFragment;
import com.jaka.recyclerviewapplication.view.adapter.contact.ContactViewHolder;
import com.jaka.recyclerviewapplication.view.adapter.contact.ContactsAdapter;
import com.jaka.recyclerviewapplication.view.swipe.RecycleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class RecyclerViewActivity extends AppCompatActivity implements RecycleItemTouchHelperCallback.RecycleItemTouchHelperListener {

    private DatabaseRepository databaseRepository;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewGroup container;
    private Handler undoHandler = new Handler(Looper.getMainLooper());
    private static long UNDO_DELAY = 5000;

    private Snackbar undoSnackbar;
    private Runnable undoRunnable = new Runnable() {
        @Override
        public void run() {
            undoSnackbar.dismiss();
        }
    };

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
        initRecyclerView();
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_contacts);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        container = findViewById(R.id.container);
        FloatingActionButton floatingActionButton = findViewById(R.id.add_contact_floating_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecyclerViewActivity.this, AddContactActivity.class));
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(contactsAdapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecycleItemTouchHelperCallback(this, 0, ItemTouchHelper.LEFT);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
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
                                swipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ContactViewHolder) {

            String name = contactsAdapter.getContactFromPosition(viewHolder.getAdapterPosition()).getFirstName();
            undoSnackbar = Snackbar
                    .make(container, name + " removed from list!", Snackbar.LENGTH_INDEFINITE);
            final Contact deletedItem = contactsAdapter.getContactFromPosition(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            contactsAdapter.removeItem(viewHolder.getAdapterPosition());

            undoSnackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    contactsAdapter.restoreItem(deletedItem, deletedIndex);
                    undoHandler.removeCallbacks(undoRunnable);
                    undoSnackbar.dismiss();
                }
            });
            undoHandler.postDelayed(undoRunnable, UNDO_DELAY);
            undoSnackbar.setActionTextColor(Color.YELLOW);
            undoSnackbar.show();
        }
    }
}
