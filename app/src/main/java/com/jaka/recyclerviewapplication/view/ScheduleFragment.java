package com.jaka.recyclerviewapplication.view;

import android.annotation.SuppressLint;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jaka.recyclerviewapplication.R;
import com.jaka.recyclerviewapplication.jobs.services.JobSchedulerService;
import com.jaka.recyclerviewapplication.view.adapter.contact.ContactsAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;

public class ScheduleFragment extends Fragment {

    public static final String DESCRIPTION_EXTRA = "description";

    private static final DateFormat DATE_WITH_YEAR_FORMAT = new SimpleDateFormat("dd MM, yyyy HH:mm:ss", Locale.getDefault());

    EditText startDate;
    EditText endDate;

    EditText description;

    private Calendar calendar = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
            new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                    startDate.setText(DATE_WITH_YEAR_FORMAT.format(calendar.getTime()));
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
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                    endDate.setText(DATE_WITH_YEAR_FORMAT.format(calendar.getTime()));
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startDate = view.findViewById(R.id.start_date_edit_text);
        endDate = view.findViewById(R.id.end_date_edit_text);
        scheduleButton = view.findViewById(R.id.button_schedule);
        description = view.findViewById(R.id.description);

        startDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    calendar = Calendar.getInstance();
                    new DatePickerDialog(v.getContext(), startDateSetListener,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                    return true;
                }
                return false;
            }
        });

        endDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    calendar = Calendar.getInstance();
                    new DatePickerDialog(v.getContext(), endDateSetListener,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                    return true;
                }
                return false;
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName serviceName = new ComponentName(v.getContext(), JobSchedulerService.class);
                JobScheduler jobScheduler = (JobScheduler) v.getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (jobScheduler != null) {
                    try {
                        Date start = DATE_WITH_YEAR_FORMAT.parse(startDate.getText().toString());
                        Date end = DATE_WITH_YEAR_FORMAT.parse(endDate.getText().toString());

                        calendar = Calendar.getInstance();


                        long tillToStart = start.getTime() - calendar.getTimeInMillis();
                        long tillToEnd = end.getTime() - calendar.getTimeInMillis();

                        PersistableBundle persistableBundle = new PersistableBundle();
                        persistableBundle.putString(DESCRIPTION_EXTRA,description.getText().toString());


                        JobInfo startJobInfo = new JobInfo.Builder(1, serviceName)
                                .setMinimumLatency(tillToStart)
                                .setExtras(persistableBundle)
                                .build();


                        JobInfo endJobInfo = new JobInfo.Builder(2, serviceName)
                                .setMinimumLatency(tillToEnd)
                                .setExtras(persistableBundle)
                                .build();

                        if (jobScheduler.schedule(startJobInfo) == JobScheduler.RESULT_SUCCESS && jobScheduler.schedule(endJobInfo) == JobScheduler.RESULT_SUCCESS) {
                            Toast.makeText(getContext(), "Notification Scheduled Successfully", Toast.LENGTH_LONG).show();
                        }

                        getFragmentManager().popBackStack();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
