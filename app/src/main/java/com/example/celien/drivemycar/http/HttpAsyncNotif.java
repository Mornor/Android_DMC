package com.example.celien.drivemycar.http;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.celien.drivemycar.adapter.CustomFragmentTabOperations;
import com.example.celien.drivemycar.core.ListSpecificCars;
import com.example.celien.drivemycar.receiver.NotificationUser;
import com.example.celien.drivemycar.tabs.TabOperations;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class HttpAsyncNotif extends AsyncTask<String, Void, JSONArray>{

    private ListSpecificCars listSpecificCarsCaller;
    private TabOperations tabOperationsCaller;
    private CustomFragmentTabOperations customFragmentTabOperationsCaller;

    public HttpAsyncNotif(ListSpecificCars caller){
        this.listSpecificCarsCaller = caller;
    }

    public HttpAsyncNotif(TabOperations caller){
        this.tabOperationsCaller = caller;
    }

    public HttpAsyncNotif(CustomFragmentTabOperations caller){
        this.customFragmentTabOperationsCaller = caller;
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
        if(params[0].equals(Action.UPDATE_REQUEST_STATE.toString()))
            return updateRequestSate(params[1], params[2]); // String of current IdNotification, and String of the choise (Action.CONFIRM_REQUEST or Action.REFUTE_REQUEST)
        return null;
    }


    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        if(listSpecificCarsCaller != null) {
            listSpecificCarsCaller.getProgressDialog().dismiss();
            listSpecificCarsCaller.onPostExecuteSendRequest(jsonArray);
        }
        if(tabOperationsCaller != null)
            tabOperationsCaller.onPostExecuteLoadNotification(jsonArray);
    }

    private JSONArray updateRequestSate(String idNotification, String actionRequested){
        boolean rentConfirmed = false;
        if(actionRequested.equals(Action.CONFIRM_RENT.toString()))
            rentConfirmed = true;
        else if(actionRequested.equals(Action.REFUTE_RENT.toString()))
            rentConfirmed = false;
        JsonParser parser = new JsonParser();
        JSONArray result = parser.updateRequestState(Integer.valueOf(idNotification), rentConfirmed, Constants.UPDATE_REQUEST_URL);
        return result; // Which is == null
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
