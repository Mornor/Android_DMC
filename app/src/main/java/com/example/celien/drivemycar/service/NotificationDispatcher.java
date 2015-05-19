package com.example.celien.drivemycar.service;

import android.app.*;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.receiver.NotificationUser;
import com.example.celien.drivemycar.utils.NotificationTypeConstants;

/** Create the right Notification depending on the server response */
public class NotificationDispatcher {

    private Notification notifCaller;

    public NotificationDispatcher(Notification notif){
        this.notifCaller = notif;
    }

    // In any case, array.getJsonObject(0).getString("type"), will be the notification type.
    public NotificationCompat.Builder createRightNotification(JSONObject object){
        try{
            switch (object.getString("notificationType")){
                case NotificationTypeConstants.CAR_REQUEST:
                    return buildNotification(object, "New car request", "Rent/Exchange request", new Intent(notifCaller, NotificationUser.class));
                case NotificationTypeConstants.REQUEST_ACCEPTED_BY_OWNER:
                    return buildNotification(object, "News from request", "Request accepted", new Intent(notifCaller, Home.class));
                case NotificationTypeConstants.REQUESTER_CHOSE_SOMEONE_ELSE:
                    return buildNotification(object, "News from request", "Not selected", new Intent());
                case NotificationTypeConstants.REQUEST_ACCEPTED_BY_BOTH_SIDES:
                    return buildNotification(object, "News from request", "Please, set your odometer", new Intent());
                case NotificationTypeConstants.OWNER_SET_ODOMETER:
                    return buildNotification(object, "News from request", "New status", new Intent());
                default:
                    break;
            }
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        // Only occurs if errors.
        return null;
    }

    private NotificationCompat.Builder buildNotification(JSONObject notifJson, String ticker, String title, Intent i){

        // Create the notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(notifCaller);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(); // Be able to set text on several lines.
        notification.setAutoCancel(true);

        // Build it
        notification.setSmallIcon(android.R.drawable.star_on);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker(ticker);
        notification.setContentTitle(title);

        // Set the text value
        try{
            String message = notifJson.getString("message");

            // Because the message is not always the same size (depending on the notification type)
            switch (notifJson.getString("notificationType")){

                case NotificationTypeConstants.CAR_REQUEST:
                    Log.d("Message", message);
                    inboxStyle.addLine(message.substring(0, 31));
                    inboxStyle.addLine(message.substring(31, 59));
                    if(message.length() < 95)
                        inboxStyle.addLine(message.substring(59, message.length()));
                    else {
                        inboxStyle.addLine(message.substring(59, 89));
                        inboxStyle.addLine(message.substring(89, 109));
                        inboxStyle.addLine(message.substring(109, message.length()));
                    }
                    break;

                case NotificationTypeConstants.REQUEST_ACCEPTED_BY_OWNER:
                    inboxStyle.addLine(message.substring(0, 31));
                    inboxStyle.addLine(message.substring(31, 61));
                    inboxStyle.addLine(message.substring(61, message.length()));
                    break;

                case NotificationTypeConstants.REQUESTER_CHOSE_SOMEONE_ELSE:
                    inboxStyle.addLine(message.substring(0, 31));
                    inboxStyle.addLine(message.substring(31, message.length()));
                    break;

                case NotificationTypeConstants.REQUEST_ACCEPTED_BY_BOTH_SIDES:
                    inboxStyle.addLine(message.substring(0, 29));
                    inboxStyle.addLine(message.substring(29, 59));
                    inboxStyle.addLine(message.substring(59, message.length()));
                    break;

                case NotificationTypeConstants.OWNER_SET_ODOMETER:
                    inboxStyle.addLine(message.substring(0, 29));
                    inboxStyle.addLine(message.substring(29, message.length()));
                    break;

                default:
                    Log.e(this.getClass().getName(), "No notification type found");
                    break;
            }

        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        // Set the inbox style.
        notification.setStyle(inboxStyle);

        PendingIntent pi = PendingIntent.getActivity(notifCaller, 0, i, PendingIntent.FLAG_UPDATE_CURRENT); // Give the phone access to the app
        notification.setContentIntent(pi);

        return notification;
    }
}
