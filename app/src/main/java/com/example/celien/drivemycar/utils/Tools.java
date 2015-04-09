package com.example.celien.drivemycar.utils;

import android.content.SharedPreferences;
import android.util.Log;

public class Tools {

    // Save username and password in SharedPreferences
    public static void saveUsernamePwd(String username, String password, SharedPreferences pref){
        SharedPreferences sharePref = pref;
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    // Retrieve username and password from SharedPreferences
    public static String[] getUsernamePassword(SharedPreferences pref){
        String usernameSaved = pref.getString("username", ""); // Return nothing ("") if usernams does not exist.
        String passwordSaved = pref.getString("password", "");
        String usernamePwd[] = new String[2];
        usernamePwd[0] = usernameSaved;
        usernamePwd[1] = passwordSaved;
        return usernamePwd;
    }

    // Clear SharedPreferences (typically, clear username/pwd when logout)
    public static void clearSharedPref(SharedPreferences pref){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.apply();
    }

    public static boolean isInteger(String str){
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c <= '/' || c >= ':') {
                return false;
            }
        }
        return true;
    }

}
