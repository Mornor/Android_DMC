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

public class NotificationService extends IntentService{

    public NotificationService(){
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("NotificationService", "About to execute MyTask");
        GetNotifs getNotifs = new GetNotifs(this);
        getNotifs.execute();
    }

    private void sendNotification(JSONArray array){
        Context context = this;
        Intent notificationIntent = new Intent(context, Login.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notif = null;
        try{
            notif = new Notification.Builder(context)
                    .setSmallIcon(android.R.drawable.star_on)
                    .setContentTitle(array.getJSONObject(0).getString("user_target_id"))
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .build();
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        //notif.flags |= Notification.FLAG_AUTO_CANCEL; // Notification is closed when clicked by the user.
        //Notification notif = new Notification(android.R.drawable.star_on, "Refresh", System.currentTimeMillis());
        //notif.setLatestEventInfo(context, "Title", "Content", contentIntent);
        notificationManager.notify(0, notif);
    }

    private class GetNotifs extends AsyncTask<String, Void, JSONArray>{
        private static final String GET_NOTIFS_URL = "http://cafca.ngrok.com/android/get_notifs";

        private NotificationService notificationServiceCaller;

        public GetNotifs(NotificationService caller){
            this.notificationServiceCaller = caller;
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            return loadNotifs();
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            if(notificationServiceCaller != null)
                notificationServiceCaller.sendNotification(array);
        }

        private JSONArray loadNotifs(){
            JsonParser parser = new JsonParser();
            return parser.getNotifications("Justine", GET_NOTIFS_URL);
        }
    }
}
