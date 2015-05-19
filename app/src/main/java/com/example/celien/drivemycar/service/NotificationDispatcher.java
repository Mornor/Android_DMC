package com.example.celien.drivemycar.service;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.receiver.NotificationUser;
import com.example.celien.drivemycar.utils.NotificationTypeConstants;

/*** Create the right Notification depending on the server response
 *
 * [TODO] TYPICALLY : MAKE THE CODE MUCH MORE EFFICIENT!
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
                case NotificationTypeConstants.REQUEST_ACCEPTED_BY_OWNER:
                    return createNotificationRequestAccepted(object);
                case NotificationTypeConstants.REQUESTER_CHOSE_SOMEONE_ELSE:
                    return createNotificationRequesterChoseSomeoneElse(object);
                case NotificationTypeConstants.REQUEST_ACCEPTED_BY_BOTH_SIDES:
                    return createNotificationRequestAcceptedBothSides(object);
                case NotificationTypeConstants.ONWER_SET_ODOMETER:
                    return createNotificationOwnerSetOdometer(object);
                default:
                    break;
            }
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        // Only occurs if errors.
        return null;
    }

    private NotificationCompat.Builder createNotificationOwnerSetOdometer(JSONObject notif){
        // Create the notification
        notification = new NotificationCompat.Builder(notifCaller);
        notification.setAutoCancel(true);

        // Build the notification
        notification.setSmallIcon(android.R.drawable.star_on);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker("New DriveMyCar request");

        try{
            notification.setContentTitle("Notification received");
            inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(notif.getString("message").substring(0, 29));
            inboxStyle.addLine(notif.getString("message").substring(29, notif.getString("message").length()));
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        notification.setStyle(inboxStyle);

        // When clicked, go to NotificationUser Activity
        Intent i = new Intent();
        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        return notification;
    }

    private NotificationCompat.Builder createNotificationRequestAcceptedBothSides(JSONObject notif){
        // Create the notification
        notification = new NotificationCompat.Builder(notifCaller);
        notification.setAutoCancel(true);

        // Build the notification
        notification.setSmallIcon(android.R.drawable.star_on);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker("New DriveMyCar request");

        try{
            notification.setContentTitle("Notification received");
            inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(notif.getString("message").substring(0, 29));
            inboxStyle.addLine(notif.getString("message").substring(29, 59));
            inboxStyle.addLine(notif.getString("message").substring(59, notif.getString("message").length()));
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        notification.setStyle(inboxStyle);

        // When clicked, go to NotificationUser Activity
        Intent i = new Intent();
        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        return notification;
    }

    private NotificationCompat.Builder createNotificationRequesterChoseSomeoneElse(JSONObject notif){
        // Create the notification
        notification = new NotificationCompat.Builder(notifCaller);
        notification.setAutoCancel(true);

        // Build the notification
        notification.setSmallIcon(android.R.drawable.star_on);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker("New DriveMyCar request");

        try{
            notification.setContentTitle("Notification received");
            inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(notif.getString("message").substring(0, 31));
            inboxStyle.addLine(notif.getString("message").substring(31, notif.getString("message").length()));
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        notification.setStyle(inboxStyle);

        // When clicked, go to NotificationUser Activity
        Intent i = new Intent();
        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        return notification;
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
            notification.setContentTitle("Notification received");
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
            notification.setContentTitle("Notification received");
            inboxStyle = new NotificationCompat.InboxStyle();
            String message = notif.getString("message");
            inboxStyle.addLine(message.substring(0, 31));
            inboxStyle.addLine(message.substring(31, 59));
            if(message.length() < 95)
                inboxStyle.addLine(message.substring(59, message.length()));
            else {
                inboxStyle.addLine(message.substring(59, 89));
                inboxStyle.addLine(message.substring(89, 109));
                inboxStyle.addLine(message.substring(109, message.length()));
            }
        } catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        notification.setStyle(inboxStyle);

        // When clicked, go to NotificationUser Activity
        Intent i = new Intent(notifCaller, NotificationUser.class);
        Bundle bdl = new Bundle();
        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        return notification;
    }
}
