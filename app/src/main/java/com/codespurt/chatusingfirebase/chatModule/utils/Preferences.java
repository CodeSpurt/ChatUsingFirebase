package com.codespurt.chatusingfirebase.chatModule.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.codespurt.chatusingfirebase.R;

/**
 * Created by Code Spurt on 12-05-18.
 */

public class Preferences {

    private Context context;
    private String fileName;
    private static SharedPreferences sharedPref;

    // params to be saved
    public static String IS_LOGGED_IN = "is_logged_in";
    public static String USERNAME = "username";
    public static String PASSWORD = "password";
    public static String DATA_SECURITY_KEY = "data_security_key";

    // boolean
    public static String TRUE = "true";
    public static String FALSE = "false";

    public Preferences() {

    }

    public Preferences(Context context) {
        this.context = context;
        fileName = context.getResources().getString(R.string.app_name);
        sharedPref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public void save(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void save(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    public String get(String key) {
        String defaultValue = "";
        return sharedPref.getString(key, defaultValue);
    }
}
