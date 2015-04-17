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
    public NotificationCompat.Builder createRightNotification(JSONArray array){
        try{
            JSONObject temp = array.getJSONObject(0);
            switch (temp.getString("notificationType")){
                case NotificationTypeConstants.LENDER_ASK_FOR_CAR:
                    return createNotificationAskForACar(array);
                case NotificationTypeConstants.OWNER_CONFIRMED_RENT:
                    return createNotificationOwnerConfirmed(array);
                case NotificationTypeConstants.OWNER_REFUTED_RENT:
                    return createNotificationOwnerRefuted(array);
            }

        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        // Only if errors.
        return null;
    }

    private NotificationCompat.Builder createNotificationOwnerConfirmed(JSONArray array){

        // Create the notification
        notification = new NotificationCompat.Builder(notifCaller);
        notification.setAutoCancel(true);

        // Build the notification
        notification.setSmallIcon(android.R.drawable.star_on);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker("New DriveMyCar request");

        try{
            JSONObject temp = array.getJSONObject(0);
            notification.setContentTitle(temp.getString("userSource")+ " confirmed your request" );
            inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine("Of course, you can rent my");
            inboxStyle.addLine(temp.getString("brand")+ " " +temp.getString("model"));
            inboxStyle.addLine("From " +temp.getString("dateFrom").substring(0, 9) + " at " +temp.getString("dateFrom").substring(10, temp.getString("dateFrom").length() - 5)+ " h");
            inboxStyle.addLine("To " +temp.getString("dateTo").substring(0, 9) + " at " +temp.getString("dateTo").substring(10, temp.getString("dateTo").length() - 5));
        }catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        notification.setStyle(inboxStyle);

        Tools.saveNotificationData(notifCaller.getSharedPreferences("transactionData", Context.MODE_PRIVATE), array, currentUsername);

        // When clicked, go to NotificationUser Activity
        Intent i = new Intent(notifCaller, Home.class);
        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        return notification;
    }

    private NotificationCompat.Builder createNotificationAskForACar(JSONArray array){

        // Create the notification
        notification = new NotificationCompat.Builder(notifCaller);
        notification.setAutoCancel(true);

        // Build the notification
        notification.setSmallIcon(android.R.drawable.star_on);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker("New DriveMyCar request");
        try{
            JSONObject temp = array.getJSONObject(0);
            notification.setContentTitle("Request from "+temp.getString("userSource"));
            inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine("Can I use your " +temp.getString("brand")+ " " +temp.getString("model"));
            inboxStyle.addLine("From " +temp.getString("dateFrom").substring(0, 9) + " at " +temp.getString("dateFrom").substring(10, temp.getString("dateFrom").length() - 5)+ " h");
            inboxStyle.addLine("To " +temp.getString("dateTo").substring(0, 9) + " at " +temp.getString("dateTo").substring(10, temp.getString("dateTo").length() - 5)+ " h ?");
        } catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        notification.setStyle(inboxStyle);

        // When clicked, go to NotificationUser Activity
        Intent i = new Intent(notifCaller, NotificationUser.class);
        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        Tools.saveNotificationData(notifCaller.getSharedPreferences("transactionData", Context.MODE_PRIVATE), array, currentUsername);

        return notification;
    }

    private NotificationCompat.Builder createNotificationOwnerRefuted(JSONArray array){
        // Create the notification
        notification = new NotificationCompat.Builder(notifCaller);
        notification.setAutoCancel(true);

        // Build the notification
        notification.setSmallIcon(android.R.drawable.star_on);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker("New DriveMyCar request");
        try{
            JSONObject temp = array.getJSONObject(0);
            Log.d("Mesage received is ", temp.getString("message"));
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
