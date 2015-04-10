package com.example.celien.drivemycar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.celien.drivemycar.service.Notification;

public class NotificationAutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, Notification.class));
    }
}
