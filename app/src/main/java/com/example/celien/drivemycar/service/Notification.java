package com.example.celien.drivemycar.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.celien.drivemycar.http.HttpAsyncJson;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;

public class Notification extends Service {

    private String username;

    private static final int UNIQUE_ID = 45452;
    private static int INTERVAL_IN_MINUTE = 1; // Change this value to change the interval of refreshment.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Retrieve the current username in SharedPref
        String userInfo[] = Tools.getUsernamePassword(getSharedPreferences("userInfo", Context.MODE_PRIVATE));

        // So, if there is a user who has already logged in before (and not logout)
        if(!userInfo[0].equals("")){
            username = userInfo[0];
            HttpAsyncJson httpAsyncJson = new HttpAsyncJson(this);
            httpAsyncJson.execute(Action.GET_NOTIFS.toString(), username, "false");
        }

        // Do not keep the service in memory if it is stopped
        stopSelf();

        // If service is killed for no reason, then do not restart it automatically.
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(
                alarmManager.RTC_WAKEUP, // Wake up device when System.currentTimeMillis() == second argument value (?)
                System.currentTimeMillis() + (1000 * 60 * INTERVAL_IN_MINUTE),
                PendingIntent.getService(this, 0, new Intent(this, Notification.class), 0));
    }

    // Retrieve the result from the DB (notification of the users) and create an android.Notification.
    public void onPostExecuteLoadNotif(JSONArray array){
            try{

                // If array contains something
                if(!array.getJSONObject(0).getBoolean("isArrayEmpty")) {

                    // Get all the notifications in DB and display them
                    for (int i = 1; i < array.length(); i++) {

                        // Notification related
                        NotificationCompat.Builder notification;

                        // Create the right notification by using the dispatcher
                        NotificationDispatcher dispatcher = new NotificationDispatcher(this, username);
                        notification = dispatcher.createRightNotification(array.getJSONObject(i));

                        // Issue notification
                        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        nm.notify(UNIQUE_ID + i, notification.build()); // Id has to be unique.
                    }
                }
            }catch (JSONException e){
                Log.e(e.getClass().getName(), "JSONException", e);
            }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
