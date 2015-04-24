package com.example.celien.drivemycar.http;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.celien.drivemycar.core.ListSpecificCars;
import com.example.celien.drivemycar.receiver.NotificationUser;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Constants;

import org.json.JSONArray;

public class HttpAsyncNotif extends AsyncTask<String, Void, JSONArray>{

    private ListSpecificCars listSpecificCarsCaller;
    private NotificationUser notificationUserCaller;

    public HttpAsyncNotif(ListSpecificCars caller){
        this.listSpecificCarsCaller = caller;
    }

    public HttpAsyncNotif(NotificationUser caller){
        this.notificationUserCaller = caller;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(listSpecificCarsCaller != null)
            listSpecificCarsCaller.setProgressDialog(ProgressDialog.show(listSpecificCarsCaller, "Please wait...", "Send request..." ));
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        if(params[0].equals(Action.SAVE_REQUEST.toString()))
            return saveRequest();
        if(params[0].equals(Action.GET_NOTIFS.toString()))
            return getNotification(params[1], params[2]); // Username and String to say that we have or not take into account that the notification has been read or not.
        return null;
    }


    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        if(listSpecificCarsCaller != null) {
            listSpecificCarsCaller.getProgressDialog().dismiss();
            listSpecificCarsCaller.onPostExecuteSendRequest(jsonArray);
        }
        if(notificationUserCaller != null){
            notificationUserCaller.onPostExecuteLoadNotification(jsonArray);
        }
    }

    private JSONArray saveRequest(){
        // List<HashMaps<String, String> is like :
        // 0 -> "owner":"celien", "brand":"bmw", "model":"335i"
        JsonParser parser = new JsonParser();
        JSONArray result = parser.saveRequest(
                Constants.SAVE_REQUEST_URL,
                listSpecificCarsCaller.getSelectedItems(),
                listSpecificCarsCaller.getUser().getUsername(),
                listSpecificCarsCaller.getDateFrom().toString(),
                listSpecificCarsCaller.getDateTo().toString(),
                listSpecificCarsCaller.isExchange());
        return result;
    }

    private JSONArray getNotification(String username, String hasToBeRead){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.getNotifications(username, Constants.GET_NOTIFS_URL, hasToBeRead);
        return result;
    }
}
