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
                case NotificationTypeConstants.WAITING_FOR_ANSWER_OF_OWNER:
                    return createNotificationAskForACar(object);
                case NotificationTypeConstants.OWNER_CONFIRMED_RENT:
                    return createNotificationOwnerConfirmed(object);
                case NotificationTypeConstants.OWNER_REFUTED_RENT:
                    return createNotificationOwnerRefuted(object);
            }

        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        // Only if errors.
        return null;
    }

    private NotificationCompat.Builder createNotificationOwnerConfirmed(JSONObject notif){

        // Create the notification
        notification = new NotificationCompat.Builder(notifCaller);
        notification.setAutoCancel(true);

        // Build the notification
        notification.setSmallIcon(android.R.drawable.star_on);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker("New DriveMyCar request");

        try{
            notification.setContentTitle(notif.getString("userSource")+ " confirmed your request" );
            inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine("Of course, you can rent my");
            inboxStyle.addLine(notif.getString("brand")+ " " +notif.getString("model"));
            inboxStyle.addLine("From " +notif.getString("dateFrom").substring(0, 9) + " at " +notif.getString("dateFrom").substring(10, notif.getString("dateFrom").length() - 5)+ " h");
            inboxStyle.addLine("To " +notif.getString("dateTo").substring(0, 9) + " at " +notif.getString("dateTo").substring(10, notif.getString("dateTo").length() - 5));

        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        notification.setStyle(inboxStyle);

        Tools.saveNotificationData(notifCaller.getSharedPreferences("transactionData", Context.MODE_PRIVATE), notif, currentUsername);

        // When clicked, go to NotificationUser Activity
        Intent i = new Intent(notifCaller, Home.class);
        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        return notification;
    }

    private NotificationCompat.Builder createNotificationAskForACar(JSONObject notif){

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
            inboxStyle.addLine("Can I use your " +notif.getString("brand")+ " " +notif.getString("model"));
            inboxStyle.addLine("From " +notif.getString("dateFrom").substring(0, 9) + " at " +notif.getString("dateFrom").substring(10, notif.getString("dateFrom").length() - 5)+ " h");
            inboxStyle.addLine("To " +notif.getString("dateTo").substring(0, 9) + " at " +notif.getString("dateTo").substring(10, notif.getString("dateTo").length() - 5)+ " h ?");
        } catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        notification.setStyle(inboxStyle);

        // When clicked, go to NotificationUser Activity
        Intent i = new Intent(notifCaller, NotificationUser.class);
        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        Tools.saveNotificationData(notifCaller.getSharedPreferences("transactionData", Context.MODE_PRIVATE), notif, currentUsername);

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
