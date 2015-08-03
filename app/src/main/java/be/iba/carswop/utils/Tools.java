package be.iba.carswop.utils;

import android.content.SharedPreferences;
import android.util.Log;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    // Clear SharedPreferences
    public static void clearSharedPrefUserLoginData(SharedPreferences pref){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.apply();
    }

    public static void clearSharedPrefUserData(SharedPreferences pref){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userSource", "");
        editor.putString("userTarget", "");
        editor.putString("brand", "");
        editor.putString("model", "");
        editor.putString("fromDate", "");
        editor.putString("toDate", "");
        editor.putString("id_transaction", "");
        editor.apply();
    }


    public static Timestamp StringAndroidToTimestamp(String s){
        Timestamp timestamp = null;
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(s);
            timestamp = new Timestamp(parsedDate.getTime());
        }catch(Exception e){
            Log.d("Exception date = ", e.toString());
        }
        return timestamp;
    }



    public static Timestamp createTimestampFromString(String dateStr, String timeStr){
        Timestamp tp = null;
        String timeWithoutSpace = timeStr.replaceAll("\\s", "");
        String timeFromToUse = timeWithoutSpace.substring(0, timeWithoutSpace.length()-1);

        try {
            String dateFromConc = dateStr +" "+timeFromToUse;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date date = dateFormat.parse(dateFromConc);
            tp = new Timestamp(date.getTime());
        }catch(Exception e){
            e.printStackTrace();
        }
        return tp;
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
