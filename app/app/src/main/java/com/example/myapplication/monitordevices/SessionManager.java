package com.example.myapplication.monitordevices;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "token";
    private static final String KEY_TOKEN = "JWT";
    private static final String KEY_NAME = "name";

    private static final String KEY_EMAIL = "email";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public void saveEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public void saveName(String name) {
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public String getName(){return sharedPreferences.getString(KEY_NAME, null);}

    public String getEmail(){return sharedPreferences.getString(KEY_EMAIL, null);}

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
}
