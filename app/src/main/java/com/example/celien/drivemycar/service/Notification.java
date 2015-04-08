package com.example.celien.drivemycar.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Notification extends Service {

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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
