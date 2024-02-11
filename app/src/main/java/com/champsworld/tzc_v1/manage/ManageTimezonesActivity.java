package com.champsworld.tzc_v1.manage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.champsworld.tzc_v1.AppSettings;
import com.champsworld.tzc_v1.R;
import com.champsworld.tzc_v1.ResultItem;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ManageTimezonesActivity extends AppCompatActivity implements TimeZoneEditor {

    private final AtomicReference<BiConsumer<Boolean,String>> editInProgress = new AtomicReference<>(null);
    private final AtomicReference<ResultItem> currentEditedItem = new AtomicReference<>(null);
    private RecyclerView resultRecyclerView;
    private ListOutTimeZonesAdapter customAdapter;
    private EditText labelEditor;
    private ListView timeZoneSelector;
    private CustomArrayAdapter timezoneArray;

    private Button updateButton;
    private Button addNewButton;
    private List<ResultItem> resultList = new ArrayList<>();
    private Map<ResultItem, Integer> locationItemMap = new HashMap<ResultItem, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_timezones);
        labelEditor = findViewById(R.id.editLocationEditText);
        timeZoneSelector = findViewById(R.id.timezoneListView);
        updateButton = findViewById(R.id.updateExistingTimeZone);
        addNewButton = findViewById(R.id.addNewTimeZone);
        Button resetButton = findViewById(R.id.resetAddUpdatePanel);
        resultRecyclerView = findViewById(R.id.timezonesRecyclerView);
        customAdapter = new ListOutTimeZonesAdapter(resultList, this);

        // Set up RecyclerView
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultRecyclerView.setAdapter(customAdapter);

        setupTimeZoneSpinner();

        initializeCurrentTargetTimezones();

        resetButton.setOnClickListener(v -> resetUI());

        addNewButton.setOnClickListener(v -> {
            if(currentEditedItem.get()==null) {
                final String newLocation = String.valueOf(labelEditor.getText());

                if (!verifyExistingKey(newLocation)) {
                    Log.i("ERROR ADD key location exists ", "  " + newLocation);
                    //returnWithResult(false, "newLocation already assigned");
                    return;
                }
                String nTz = timezoneArray.getSelectedItemString();
                if (TextUtils.isEmpty(newLocation) || nTz==null || TextUtils.isEmpty(nTz.trim())) {
                    Log.i("ERROR ADD key location Timezone EMPTY ", "  " + newLocation);
                    return; // or break, continue, throw
                }

                if (AppSettings.addTimeZoneSetting(this, newLocation.trim(), nTz.trim())) {
                    ResultItem newResultItem = new ResultItem(newLocation, nTz);

                    resultList.add( newResultItem);
                    //swapping the index will introduce no shifting
                    int size = resultList.size();
                    locationItemMap.put(newResultItem, size);
                    customAdapter.notifyItemInserted(size);
                }
            } else {
                Log.i("ADD", "Failed Editing is in progress");
            }
        });

        updateButton.setOnClickListener(v -> {
            final ResultItem currentResultItem = currentEditedItem.get();
            if (currentResultItem == null) {
                Log.i("ERROR update NULL ", " NOTHING TO UPDATE INTERNAL");
                returnWithResult(false, "NOTHING TO UPDATE INTERNAL");
                return;
            }
            final String oldLocation = currentResultItem.getLocation();
            final String newLocation = String.valueOf(labelEditor.getText());

            if (!verifyExistingKey(newLocation)) {
                Log.i("ERROR update key location exists ", "  " + newLocation + " old " + oldLocation);
                returnWithResult(false, "newLocation already assigned");
                return;
            }

            if(!locationItemMap.containsKey(currentResultItem)) {
                Log.i("ERROR update existing editedlist item index not found ", " NOT ABLE TO UPDATE INTERNAL");
                returnWithResult(false, "NOT ABLE TO UPDATE INTERNAL");
            } else {
                // already inside else the ignore warning
                final int index = locationItemMap.get(currentResultItem);
                String nTz = timezoneArray.getSelectedItemString();
                if (nTz==null || TextUtils.isEmpty(nTz.trim())) {
                    Log.i("ERROR Update key location Timezone EMPTY ", "  " + newLocation);
                    nTz = currentResultItem.getTimezone();
                }
                if (AppSettings.addTimeZoneSetting(this, newLocation, nTz)) {
                    boolean delPrev = AppSettings.deleteTimeZoneSetting(this, oldLocation);
                    if (!delPrev) {
                        Log.i("ERROR DELETING OLD LOCATION ", " old " + oldLocation + "  new " + newLocation);
                    }
                    ResultItem newResultItem = new ResultItem(newLocation, currentResultItem.getTimezone());
                    //swapping the index will introduce no shifting
                    locationItemMap.put(newResultItem, index);
                    locationItemMap.remove(currentResultItem);
                    resultList.set(index, newResultItem);
                    customAdapter.notifyItemChanged(index);
                    returnWithResult(true, "Success");
                    resetUI();
                } else
                    Log.i("ERROR ADDING NEW LOCATION ", "new " + newLocation + " old " + oldLocation);
            }
        });
    }

    private void returnWithResult(boolean t, String message) {
        try {
            editInProgress.get().accept(t, message);
        } catch (Throwable e) {
            Log.e("ERROR SETTING UPD RESULT", e.getMessage());
        }
    }

    private boolean verifyExistingKey(String location) {
        return !AppSettings.isTimeZoneSettingExists(this, location);
    }

    private void resetUI() {
        if(editInProgress.compareAndSet(null, null)) return;
        editInProgress.set(null);
        labelEditor.setText("");
        addNewButton.setEnabled(true);
        updateButton.setEnabled(false);
        currentEditedItem.set(null);
    }

    private void setupTimeZoneSpinner() {
        String[] availableTimeZoneIds = TimeZone.getAvailableIDs();

// Create an ArrayAdapter using the available time zone IDs array and a default spinner layout
        timezoneArray = new CustomArrayAdapter(
                this, android.R.layout.simple_list_item_1, availableTimeZoneIds
        );

// Specify the layout to use when the list of choices appears
        //timezoneArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        timeZoneSelector.setAdapter(timezoneArray);

        timeZoneSelector.setOnItemClickListener((parent, view, position, id) -> {
            // Set the selected item position
            timezoneArray.setSelectedItem(position);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeCurrentTargetTimezones() {
        resultList.clear();
        locationItemMap.clear();
        int i = 0;
        Map<String, ZoneId> zones = AppSettings.getOutTimeZones();
        for (Map.Entry<String, ZoneId> entry : zones.entrySet()) {
            ResultItem resultItem = new ResultItem(entry.getKey(), entry.getValue().getId());
            resultList.add(resultItem);
            locationItemMap.put(resultItem, i++);
        }
        customAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadItem(ResultItem item) {

    }

    @Override
    public void editItem(ResultItem item, BiConsumer<Boolean, String> onUpdateFinished) {
        addNewButton.setEnabled(false);
        updateButton.setEnabled(true);

        editInProgress.set(onUpdateFinished);
        currentEditedItem.set(item);
        labelEditor.setText(item.getLocation());
        final int pos = timezoneArray.getPosition(item.getTimezone());
        Log.d("SELECTED ITEM FOR EDIT ", " pos "+pos+" item location "+item.getLocation() +" tz "+item.getTimezone());
        timezoneArray.setSelectedItem(pos);
        timeZoneSelector.setSelection(pos);
    }

    @Override
    public boolean deleteItem(ResultItem item, BiConsumer<Boolean, String> onDeleteFinished) {

        if(!locationItemMap.containsKey(item)) {
            Log.i("ERROR delete existing editedlist item index not found ", " NOT ABLE TO DELETE INTERNAL");
            returnWithResult(false, "NOT ABLE TO DELETE INTERNAL");
            return false;
        } else {
            // already inside if else the ignore warning
            final int index = locationItemMap.get(item);
            boolean delPrev = AppSettings.deleteTimeZoneSetting(this, item.getLocation());
            if (!delPrev) {
                Log.i("ERROR DELETING OLD LOCATION ", " old " + item.getLocation() );
                returnWithResult(false, "NOT ABLE TO DLETE OLD LOCATION ");
            } else {
                resultList.remove(index);
                customAdapter.notifyItemRemoved(index);
                reIndexLocationItemMap();
                returnWithResult(true, "Success");
            }
        }
        return true;
    }

    void reIndexLocationItemMap(){
        locationItemMap.clear();
        int i=0;
        for(ResultItem item: resultList){
            locationItemMap.put(item, i++);
        }
    }
}