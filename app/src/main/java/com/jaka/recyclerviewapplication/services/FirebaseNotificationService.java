package com.jaka.recyclerviewapplication.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaka.recyclerviewapplication.App;
import com.jaka.recyclerviewapplication.model.Contact;
import com.jaka.recyclerviewapplication.notifications.ScheduleNotificationManager;

import androidx.annotation.Nullable;

public class FirebaseNotificationService extends Service {

    private ListenerRegistration listenerRegistration;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listenerRegistration = App.getInstance().getFirebaseCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            ScheduleNotificationManager.showNotification(getApplicationContext(), 1, "Schedule", "New item Added", dc.getDocument().toObject(Contact.class).toString());
                        } else if (dc.getType() == DocumentChange.Type.MODIFIED) {
                            ScheduleNotificationManager.showNotification(getApplicationContext(), 1, "Schedule", "Item Modified", dc.getDocument().toObject(Contact.class).toString());
                        }
                    }
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
