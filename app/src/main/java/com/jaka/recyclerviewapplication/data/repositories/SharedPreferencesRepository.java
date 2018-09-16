package com.jaka.recyclerviewapplication.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesRepository {

    private static final int PREFERENCES_MODE = Context.MODE_PRIVATE;
    private static final String PREFERENCES_NAME = "CONTACTS";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, PREFERENCES_MODE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    public static void saveStringPreference(Context context, String key, String preference) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, preference);
        editor.apply();
    }

}
