package com.jaka.recyclerviewapplication.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.jaka.recyclerviewapplication.R;
import com.jaka.recyclerviewapplication.jobs.services.JobSchedulerService;
import com.jaka.recyclerviewapplication.view.adapter.contact.ContactsAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScheduleFragment extends Fragment {

    public static final String START_EXTRA = "START";
    public static final String END_EXTRA = "END";

    EditText startDate;
    EditText endDate;

    private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    private Calendar calendar = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
            new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                    startDate.setText(calendar.getTime().toString());
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }
    };

    private DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
            new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                    endDate.setText(calendar.getTime().toString());
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }
    };

    Button scheduleButton;

    public ScheduleFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_date, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startDate = view.findViewById(R.id.start_date_edit_text);
        endDate = view.findViewById(R.id.end_date_edit_text);
        scheduleButton = view.findViewById(R.id.button_schedule);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                new DatePickerDialog(v.getContext(), startDateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                new DatePickerDialog(v.getContext(), endDateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName serviceName = new ComponentName(v.getContext(), JobSchedulerService.class);
                JobScheduler jobScheduler = (JobScheduler) v.getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (jobScheduler != null) {
                    try {
                        Date start = dateFormat.parse(startDate.getText().toString());
                        Date end = dateFormat.parse(endDate.getText().toString());

                        calendar = Calendar.getInstance();


                        JobInfo startJobInfo = new JobInfo.Builder(1, serviceName)
                                .setMinimumLatency(start.getTime() - calendar.getTimeInMillis())
                                .build();


                        JobInfo endJobInfo = new JobInfo.Builder(2, serviceName)
                                .setMinimumLatency(end.getTime() - calendar.getTimeInMillis())
                                .build();

                        jobScheduler.schedule(startJobInfo);
                        jobScheduler.schedule(endJobInfo);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
