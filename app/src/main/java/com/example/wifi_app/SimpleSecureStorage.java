package com.example.wifi_app;

import android.content.Context;
import android.content.SharedPreferences;

public class SimpleSecureStorage {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private final SharedPreferences sharedPreferences;

    public SimpleSecureStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void storeCredentials(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public String getUsername() {



        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }
}
