package com.app.sample.fchat.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.sample.fchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Bibaswann on 20-02-2017.
 */

public class SettingsAPI {
    Context mContext;
    private SharedPreferences sharedSettings;

    public SettingsAPI(Context context) {
        mContext = context;
        sharedSettings = mContext.getSharedPreferences(mContext.getString(R.string.settings_file_name), Context.MODE_PRIVATE);
    }

    public String readSetting(String key) {
        return sharedSettings.getString(key, "na");
    }

    public void addUpdateSettings(String key, String value) {
        SharedPreferences.Editor editor = sharedSettings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void deleteAllSettings() {
        sharedSettings.edit().clear().apply();
    }

    public List<String> readAll() {
        List<String> allUser = new ArrayList<>();
        Map<String, ?> allEntries = sharedSettings.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().contains("@"))
                allUser.add(entry.getKey() + " (" + entry.getValue() + ")");
        }
        return allUser;
    }
}
