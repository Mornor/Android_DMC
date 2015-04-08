package com.example.celien.drivemycar.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.celien.drivemycar.core.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Notification extends Service {

    // Notification related
    NotificationCompat.Builder notification;
    private static final int UNIQUE_ID = 45452;
    private static int INTERVAL_IN_MINUTE = 15; // Change this value to change the interval of refreshment.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Do not want to keep the service in memory is it is stopped
        stopSelf();

        // If service is killed for no reason, then do not restart it automatically.
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(
                alarmManager.RTC_WAKEUP, // Wake up device when System.currentTimeMillis() == second arrgument value (?)
                System.currentTimeMillis() + (1000 * 60 * INTERVAL_IN_MINUTE),
                PendingIntent.getService(this, 0, new Intent(this, Notification.class), 0));
    }

    // Retrieve the result from the DB (notification of the users) and create an android.Notification.
    public void onPostExecuteLoadNotif(JSONArray array){
        // If array.length == 0, then there is no notifications to display
        if(array.length() != 0){
            // Create the notification
            notification = new NotificationCompat.Builder(this);
            notification.setAutoCancel(true);

            // Build the notification
            notification.setSmallIcon(android.R.drawable.star_on);
            notification.setWhen(System.currentTimeMillis());
            notification.setTicker("New DriveMyCar request");
            try{
                JSONObject temp = array.getJSONObject(0);
                notification.setContentTitle("New request from "+temp.getString("userSource"));
                notification.setContentText(temp.getString("message"));
            } catch(JSONException e){
                Log.e(e.getClass().getName(), "JSONException", e);
            }

            // When clicked, go back to Login Activity
            Intent i = new Intent(this, Login.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
            notification.setContentIntent(pi);

            // Issue notification
            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            nm.notify(UNIQUE_ID, notification.build());

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
