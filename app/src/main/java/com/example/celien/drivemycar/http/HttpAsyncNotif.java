package com.example.celien.drivemycar.http;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.accessibility.AccessibilityRecord;

import com.example.celien.drivemycar.adapter.CustomRequestReceived;
import com.example.celien.drivemycar.core.ListSpecificCars;
import com.example.celien.drivemycar.core.RequestData;
import com.example.celien.drivemycar.core.RequestReceived;
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
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class HttpAsyncNotif extends AsyncTask<String, Void, JSONArray>{

    private ListSpecificCars listSpecificCarsCaller;
    private RequestReceived requestReceivedCaller;
    private CustomRequestReceived customRequestReceivedCaller;
    private FragmentActivity activity;
    private TabOperations tabOperationsCaller;
    private RequestData requestDataCaller;

    public HttpAsyncNotif(ListSpecificCars caller){
        this.listSpecificCarsCaller = caller;
    }

    public HttpAsyncNotif(RequestReceived caller){
        this.requestReceivedCaller = caller;
    }

    public HttpAsyncNotif(CustomRequestReceived caller){
        this.customRequestReceivedCaller = caller;
    }

    public HttpAsyncNotif(FragmentActivity act, TabOperations caller){
        this.activity = act;
        this.tabOperationsCaller = caller;
    }

    public HttpAsyncNotif(RequestData caller){
        this.requestDataCaller = caller;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(listSpecificCarsCaller != null)
            listSpecificCarsCaller.setProgressDialog(ProgressDialog.show(listSpecificCarsCaller, "Please wait...", "Send request..." ));
        if(tabOperationsCaller != null)
            tabOperationsCaller.setProgressDialog(ProgressDialog.show(tabOperationsCaller.getActivity(), "Please wait...", "Fetch requests..."));
        if(requestDataCaller != null)
            requestDataCaller.setProgressDialog(ProgressDialog.show(requestDataCaller, "Please wait...", "Fetch requests..."));
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        if(params[0].equals(Action.SAVE_REQUEST.toString()))
            return saveRequest();
        if(params[0].equals(Action.GET_NOTIFS.toString()))
            return getNotification(params[1], params[2]); // Username and String to say that we have or not take into account that the notification has been read or not.
        if(params[0].equals(Action.UPDATE_REQUEST_STATE.toString()))
            return updateRequestSate(params[1], params[2]); // String of current IdNotification, and String of the choice (Action.CONFIRM_REQUEST or Action.REFUTE_REQUEST)
        if(params[0].equals(Action.GET_REQUEST_BY_DATE.toString()))
            return getRequestByDate(params[1]);
        if(params[0].equals(Action.GET_REQUEST_DATA.toString()))
            return getRequestData(params[1], params[2], params[3]); // username, fromDate, toDate
        return null;
    }


    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        if(listSpecificCarsCaller != null) {
            listSpecificCarsCaller.getProgressDialog().dismiss();
            listSpecificCarsCaller.onPostExecuteSendRequest(jsonArray);
        }
        if(tabOperationsCaller != null){
            tabOperationsCaller.getProgressDialog().dismiss();
            tabOperationsCaller.onPostExecuteLoadRequestByDate(jsonArray);
        }
        if(requestReceivedCaller != null)
            requestReceivedCaller.onPostExecuteLoadNotification(jsonArray);
        if(requestDataCaller != null) {
            requestDataCaller.getProgressDialog().dismiss();
            requestDataCaller.onPostExecuteLoadRequestData(jsonArray);
        }
    }

    private JSONArray getRequestByDate(String username){
        return new JsonParser().getRequestsByDate(username, Constants.GET_REQUEST_BY_DATE);
    }

    private JSONArray getRequestData(String username, String fromDate, String toDate){
        return new JsonParser().getRequestData(username, fromDate, toDate, Constants.GET_REQUEST_DATA);
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
