package com.champsworld.tzc_v1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.champsworld.tzc_v1.manage.ManageTimezonesActivity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private static final ZoneId SOURCE_TIME_ZONE = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd (z)");
    private final AtomicInteger selectedHour = new AtomicInteger(0);
    private final AtomicInteger selectedMinute = new AtomicInteger(0);
    private Button selectDateButton;
    private int mYear, mMonth, mDay;
    private TargetOutListTimeAdapter customAdapter;
    private List<ResultItem> resultList;
    private Map<String, ZoneId> targetZoneIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPreferences();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner hourSpinner = findViewById(R.id.hourSpinner);
        Spinner minuteSpinner = findViewById(R.id.minuteSpinner);
        selectDateButton = findViewById(R.id.selectDateButton);
        Button convertButton = findViewById(R.id.convertButton);
        // resultListView = findViewById(R.id.resultListView);

        RecyclerView resultRecyclerView = findViewById(R.id.resultRecyclerView);

        ArrayAdapter<CharSequence> hourAdapter = ArrayAdapter.createFromResource(this,
                R.array.hours, android.R.layout.simple_spinner_item);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        hourSpinner.setAdapter(hourAdapter);

        initDefaultHour(hourSpinner, hourAdapter);

        ArrayAdapter<CharSequence> minuteAdapter = ArrayAdapter.createFromResource(this,
                R.array.minutes, android.R.layout.simple_spinner_item);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteSpinner.setAdapter(minuteAdapter);

        resultList = new ArrayList<>();
        customAdapter = new TargetOutListTimeAdapter(resultList);

        // Set up RecyclerView
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultRecyclerView.setAdapter(customAdapter);

        initializeMonthDayYear();



        hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected hour value
                selectedHour.set(Integer.parseInt((String) parent.getItemAtPosition(position)));
                // Use the selected hour value as needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
                // mid day is easiest
                selectedHour.set(12);
            }
        });

        minuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected minute value
                selectedMinute.set(Integer.parseInt((String) parent.getItemAtPosition(position)));
                // Use the selected minute value as needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
                selectedMinute.set(0);
            }
        });

        selectDateButton.setOnClickListener(v -> {
            // Get current date
            // Create a new DatePickerDialog and show it
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Update the selectDateButton text with the selected date
                        setDateText(year, monthOfYear, dayOfMonth);
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        convertButton.setOnClickListener(v -> {
            resultList.clear();
            Log.d( "ConvertOnClick ","MONTH " + mMonth + " year " + mYear + " day " + mDay);
            LocalDateTime localDateTime = LocalDateTime.of(mYear, mMonth + 1, mDay, selectedHour.get(), selectedMinute.get());
            ZonedDateTime sourceZonedDateTime = localDateTime.atZone(SOURCE_TIME_ZONE);
            for (Map.Entry<String, ZoneId> entry : AppSettings.getOutTimeZones().entrySet()) {
                ZonedDateTime targetZonedDateTime = sourceZonedDateTime.withZoneSameInstant(entry.getValue());
                resultList.add(new ResultItem(entry.getKey(), targetZonedDateTime.format(TIME_FORMATTER), entry.getValue().getId(), targetZonedDateTime.format(DATE_FORMATTER)));
            }
            // Notify the adapter that the data set has changed
            customAdapter.notifyDataSetChanged();
        });
    }

    private void initDefaultHour(Spinner hourSpinner, ArrayAdapter<CharSequence> hourAdapter) {
        LocalTime time = LocalTime.now();
        selectedHour.set(time.getHour());
        int pos = hourAdapter.getPosition(""+selectedHour.get());
        hourSpinner.setSelection(pos);
    }

    private void setDateText(int year, int monthOfYear, int dayOfMonth) {
        this.mMonth = monthOfYear;
        this.mYear = year;
        this.mDay = dayOfMonth;
        Log.d("SETDATE ", "MONTH " + mMonth + " year " + mYear + " day " + mDay);
        selectDateButton.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
    }

    private void initializeMonthDayYear() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        setDateText(mYear, mMonth, mDay);
        Log.d("INITDATE MONTH ", mMonth + " year " + mYear + " day " + mDay);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setup_timezones) {
            // Handle "Setup timezones" menu item click
            Intent intent = new Intent(this, ManageTimezonesActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initPreferences() {
        targetZoneIds = AppSettings.initializeSettings(this);
    }
}