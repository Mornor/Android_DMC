package com.example.celien.drivemycar.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.celien.drivemycar.core.Login;
import com.example.celien.drivemycar.http.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationService extends IntentService{

    public NotificationService(){
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("NotificationService", "About to execute MyTask");
        //new getNotifs().execute();
        //this.sendNotification(this);

    }

    private void sendNotification(Context context){
        Intent notificationIntent = new Intent(context, Login.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Notification notif = new Notification.Builder(context)
        //        .setSmallIcon(android.R.drawable.star_on)
        //        .setContentTitle("Hello")
        //        .setWhen(System.currentTimeMillis())
        //        .build();
        Notification notif = new Notification(android.R.drawable.star_on, "Refresh", System.currentTimeMillis());
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.setLatestEventInfo(context, "Title", "Content", contentIntent);
        notificationManager.notify(0, notif);
    }

    private class getNotifs extends AsyncTask<String, Void, JSONArray>{
        private static final String GET_NOTIFS_URL = "http://cafca.ngrok.com/android/get_notifs";

        @Override
        protected JSONArray doInBackground(String... params) {
            return loadNotifs();
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            printResult(array);
        }

        private JSONArray loadNotifs(){
            JsonParser parser = new JsonParser();
            return parser.getNotifications("Justine", GET_NOTIFS_URL);
        }

        private void printResult(JSONArray array){
            Log.d("***", "***");
            try {
                for (int i = 0; i < array.length(); i++) {
                    Log.d("Result source ", array.getJSONObject(i).getString("userSource"));
                    Log.d("Result message", array.getJSONObject(i).getString("message"));
                }
            }catch(JSONException e){
             Log.e(e.getClass().getName(), "JSONException", e);
            }
        }
    }
}
