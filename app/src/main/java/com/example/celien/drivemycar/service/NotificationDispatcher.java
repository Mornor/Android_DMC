package com.example.celien.drivemycar.service;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.receiver.NotificationUser;
import com.example.celien.drivemycar.utils.NotificationTypeConstants;
import com.example.celien.drivemycar.utils.Tools;

/*** Create the right Notification depending on the server response
 **/
public class NotificationDispatcher {

    private NotificationCompat.Builder notification;
    private NotificationCompat.InboxStyle inboxStyle;
    private Notification notifCaller;
    private String currentUsername;

    public NotificationDispatcher(Notification notif, String currentUsername){
        this.notifCaller = notif;
        this.currentUsername = currentUsername;
    }

    // In any case, array.getJsonObject(0).getString("type"), will be the notification type.
    public NotificationCompat.Builder createRightNotification(JSONObject object){
        try{
            switch (object.getString("notificationType")){
                case NotificationTypeConstants.CAR_REQUEST:
                    return createNotificationCarRequest(object);
                case NotificationTypeConstants.REQUEST_ACCEPTED:
                    return createNotificationRequestAccepted(object);
                default:
                    break;
            }
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        // Only if errors.
        return null;
    }

    private NotificationCompat.Builder createNotificationRequestAccepted(JSONObject notif){

        // Create the notification
        notification = new NotificationCompat.Builder(notifCaller);
        notification.setAutoCancel(true);

        // Build the notification
        notification.setSmallIcon(android.R.drawable.star_on);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker("New DriveMyCar request");

        try{
            notification.setContentTitle("Request from "+notif.getString("userSource"));
            inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(notif.getString("message").substring(0, 31));
            inboxStyle.addLine(notif.getString("message").substring(31, 61));
            inboxStyle.addLine(notif.getString("message").substring(61, notif.getString("message").length()));
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        notification.setStyle(inboxStyle);

        // When clicked, go to NotificationUser Activity
        Intent i = new Intent(notifCaller, Home.class);
        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        return notification;
    }

    private NotificationCompat.Builder createNotificationCarRequest(JSONObject notif){

        // Create the notification
        notification = new NotificationCompat.Builder(notifCaller);
        notification.setAutoCancel(true);

        // Build the notification
        notification.setSmallIcon(android.R.drawable.star_on);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker("New DriveMyCar request");
        try{
            notification.setContentTitle("Request from "+notif.getString("userSource"));
            inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(notif.getString("message").substring(0, 31));
            inboxStyle.addLine(notif.getString("message").substring(31, 61));
            inboxStyle.addLine(notif.getString("message").substring(61, notif.getString("message").length()));
        } catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        notification.setStyle(inboxStyle);

        // When clicked, go to NotificationUser Activity
        Intent i = new Intent(notifCaller, NotificationUser.class);
        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        return notification;
    }

    private NotificationCompat.Builder createNotificationOwnerRefuted(JSONObject notif){
        // Create the notification
        notification = new NotificationCompat.Builder(notifCaller);
        notification.setAutoCancel(true);

        // Build the notification
        notification.setSmallIcon(android.R.drawable.star_on);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker("New DriveMyCar request");
        try{
            notification.setContentTitle("Request from "+notif.getString("userSource"));
            inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine("Sorry "+currentUsername+", but I can't rent you my");
            inboxStyle.addLine(notif.get("brand")+ " " +notif.getString("model")+ " this time.");
        } catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        notification.setStyle(inboxStyle);

        // When click on notification, do absolutely nothing.
        Intent i = new Intent();
        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        return notification;
    }
}
