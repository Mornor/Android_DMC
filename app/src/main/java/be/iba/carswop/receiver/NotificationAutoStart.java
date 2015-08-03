package be.iba.carswop.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import be.iba.carswop.service.Notification;

public class NotificationAutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, Notification.class));
    }
}
