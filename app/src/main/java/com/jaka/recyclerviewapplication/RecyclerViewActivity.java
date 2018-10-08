package com.jaka.recyclerviewapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Arrays;
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
    private static int SIGN_IN = 100;

    private List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build());


    private Snackbar undoSnackbar;
    private Runnable undoRunnable;

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

        if (App.getInstance().getFirebaseAuth().getCurrentUser() != null) {
            databaseRepository = new DatabaseRepository(App.getInstance().getDatabase());
            refresh();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = App.getInstance().getFirebaseAuth().getCurrentUser();
                databaseRepository = new DatabaseRepository(App.getInstance().getDatabase());
                refresh();
            } else {
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseRepository.quit();
    }

    private void refresh() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.a_recycler, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivityForResult(
                                        AuthUI.getInstance()
                                                .createSignInIntentBuilder()
                                                .setAvailableProviders(providers)
                                                .build(),
                                        SIGN_IN);
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ContactViewHolder) {

            Contact contact = contactsAdapter.getContactFromPosition(viewHolder.getAdapterPosition());
            String name = contact.getFirstName();
            undoSnackbar = Snackbar
                    .make(container, name + " removed from list!", Snackbar.LENGTH_INDEFINITE);
            final Contact deletedItem = contactsAdapter.getContactFromPosition(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            contactsAdapter.removeItem(viewHolder.getAdapterPosition());

            undoSnackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contactsAdapter.restoreItem(deletedItem, deletedIndex);
                    undoHandler.removeCallbacks(undoRunnable);
                    undoSnackbar.dismiss();
                }
            });
            initUndoRunnable(contact);
            undoHandler.postDelayed(undoRunnable, UNDO_DELAY);
            undoSnackbar.setActionTextColor(Color.YELLOW);
            undoSnackbar.show();
        }
    }

    private void initUndoRunnable(final Contact contact) {
        undoRunnable = new Runnable() {
            @Override
            public void run() {
                undoSnackbar.dismiss();
                App.getInstance().getFirebaseCollection()
                        .document(contact.getRemoteId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseRepository.start(new Handler(getMainLooper()) {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        switch (msg.what) {
                                            case Loader.LOADING_COMPLETE:
                                                if (msg.obj != null) {
                                                    if (msg.obj instanceof List) {
                                                        Snackbar.make(container, "Removed", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                });
                                databaseRepository.removeContact(contact);
                            }
                        });
            }
        };
    }
}
