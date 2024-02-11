package com.champsworld.tzc_v1;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.champsworld.tzc_v1.manage.ManageTimezonesActivity;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AppSettings {
    public static final String OUT = "_out_.";
    public static final String APP_FIRST_TIME = "_app_.firstTime";
    private static final String PREFS_NAME = "tzc_prefs_v1";
    private static final String[] output_timezones = new String[]{"Asia/Dubai", "Asia/Riyadh", "America/New_York", "Europe/Amsterdam", "Europe/London",
            "Pacific/Fiji", "Pacific/Port_Moresby", "America/Chicago"};
    private static AtomicReference<Map<String, ZoneId>> sharedZoneIdsMap= new AtomicReference<>(null);

    private static List<ZoneId> prepareZoneList() {

        List<ZoneId> zList = new ArrayList<>(11);
        for (String zId : output_timezones) {
            ZoneId sourceTimeZone = ZoneId.of(zId);
            zList.add(sourceTimeZone);
        }
        return zList;
    }

    private static Map<String, ZoneId> initOutputTimeZoneMap() {
        Map<String, ZoneId> locationTimeZoneMapping = new HashMap<>();
        locationTimeZoneMapping.put("New York", ZoneId.of("America/New_York"));
        locationTimeZoneMapping.put("Texas", ZoneId.of("America/Chicago"));
        locationTimeZoneMapping.put("Netherlands", ZoneId.of("Europe/Amsterdam"));
        locationTimeZoneMapping.put("UK", ZoneId.of("Europe/London"));
        locationTimeZoneMapping.put("Dubai", ZoneId.of("Asia/Dubai"));
        locationTimeZoneMapping.put("Saudi Arabia", ZoneId.of("Asia/Riyadh"));
        locationTimeZoneMapping.put("Fiji", ZoneId.of("Pacific/Fiji"));
        locationTimeZoneMapping.put("PNG", ZoneId.of("Pacific/Port_Moresby"));
        return locationTimeZoneMapping;
    }

    public static Map<String, ZoneId> initializeSettings(Context context ) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        //SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        boolean firstTime = prefs.getBoolean(APP_FIRST_TIME, true);
        Map<String, ?> allPrefs = prefs.getAll();
        Log.i("FIRST TIME VALUE" , " "+firstTime + " Size "+allPrefs.size());

        if (firstTime || allPrefs.size() <=1) {
            final Map<String, ZoneId> zoneIdsMap = loadInitialTimeZone(context);
            sharedZoneIdsMap.set(zoneIdsMap);
            // Mark that the app has been opened for the first time
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(APP_FIRST_TIME, false);
            editor.apply();
        } else {
            sharedZoneIdsMap.set(new HashMap<>());
            for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(OUT)) {
                    Object value = entry.getValue();
                    String substring = key.substring(OUT.length());
                    sharedZoneIdsMap.get().put(substring, ZoneId.of((String) value));
                    Log.d("TimeZonePrefsLOAD", key + " : " + value.toString()+" newKey "+substring);
                }
            }
        }

        return getOutTimeZones();
    }

    @NonNull
    private static Map<String, ZoneId> loadInitialTimeZone(Context context) {
        final Map<String, ZoneId> zoneIdsMap = initOutputTimeZoneMap();
        // Save your default settings using AppSettings.saveSettings()
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        for (Map.Entry<String, ZoneId> entry : zoneIdsMap.entrySet()) {
            saveSettings(editor, context, OUT + entry.getKey(), entry.getValue().getId());
        }
        editor.apply();
        return zoneIdsMap;
    }

    public static boolean deleteTimeZoneSetting(Context context, String key) {

        String pref_key = OUT + key;
        Log.i("DELETE TIMEZONE ", " key "+ key +" pre key "+pref_key );
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.contains(pref_key)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(pref_key);
            editor.apply();
            editor.commit();
            sharedZoneIdsMap.get().remove(key);
            return true;
        }
        return false;
    }

    public static void saveSettings(SharedPreferences.Editor editor, Context context, String key, String value) {
        // SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSetting(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(key, null); // Return null if the key is not found
    }

    public static Map<String, ZoneId> getOutTimeZones() {
        return sharedZoneIdsMap.get();
    }

    public static boolean addTimeZoneSetting(Context context, String key, String timezone) {
        String pref_key = OUT + key;
        Log.i("ADD TIMEZONE ", " key "+ key +" pre key "+pref_key +" value "+timezone);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (!prefs.contains(pref_key)) {
            SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            saveSettings(editor, context, pref_key, timezone);
            editor.apply();
            sharedZoneIdsMap.get().put(key, ZoneId.of(timezone));
            return true;
        }
        return false;
    }


    public static boolean isTimeZoneSettingExists(Context context, String key) {
        String pref_key = OUT + key;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.contains(pref_key);
    }
}