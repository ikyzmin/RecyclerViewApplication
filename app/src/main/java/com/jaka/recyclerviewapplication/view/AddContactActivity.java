package com.jaka.recyclerviewapplication.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jaka.recyclerviewapplication.App;
import com.jaka.recyclerviewapplication.R;
import com.jaka.recyclerviewapplication.async.Loader;
import com.jaka.recyclerviewapplication.data.repositories.DatabaseRepository;
import com.jaka.recyclerviewapplication.model.Contact;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AddContactActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText phoneNumberEditText;

    private DatabaseRepository databaseRepository;

    private Button okButton;

    @Override
    protected void onResume() {
        super.onResume();
        databaseRepository = new DatabaseRepository(App.getInstance().getDatabase());
        databaseRepository.start(new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Loader.LOADING_COMPLETE:
                        finish();
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f_add_contact);
        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        okButton = findViewById(R.id.add_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact newContact = new Contact.Builder()
                        .firstName(firstNameEditText.getText().toString())
                        .lastName(lastNameEditText.getText().toString())
                        .phoneNumber(phoneNumberEditText.getText().toString())
                        .build();
                databaseRepository.insertContact(newContact);
                finish();
            }
        });
    }
}
