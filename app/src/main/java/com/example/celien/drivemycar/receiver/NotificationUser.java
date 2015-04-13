package com.example.celien.drivemycar.receiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.utils.Tools;

/*** This class create a User when the notification has been clicked and launch an Intent
 * to Home activity. **/
public class NotificationUser extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String username = "";

        // Retrieve the current username in SharedPref
        String userInfo[] = Tools.getUsernamePassword(getSharedPreferences("userInfo", Context.MODE_PRIVATE));

        // So, if there is a user who has already logged in before (and not logout)
        if (!userInfo[0].equals(""))
            username = userInfo[0];

        Log.d("Username notification ", username);
    }

}
