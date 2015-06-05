package com.example.celien.drivemycar.http;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.celien.drivemycar.adapter.CustomRequestReceived;
import com.example.celien.drivemycar.core.ListSpecificCars;
import com.example.celien.drivemycar.core.RequestData;
import com.example.celien.drivemycar.core.RequestReceived;
import com.example.celien.drivemycar.core.SelectOwner;
import com.example.celien.drivemycar.tabs.TabOperations;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Constants;
import org.json.JSONArray;

public class HttpAsyncNotif extends AsyncTask<Action, Void, JSONArray>{

    private ListSpecificCars listSpecificCarsCaller;
    private RequestReceived requestReceivedCaller;
    private CustomRequestReceived customRequestReceivedCaller;
    private FragmentActivity activity;
    private TabOperations tabOperationsCaller;
    private RequestData requestDataCaller;

    // If true, then we notify th selected owner from selectedOwnerCaller.
    // If false, we need to send request from selectedOwner to display the agreed owners.
    private boolean notifySelectedOwner;
    private SelectOwner selectOwnerCaller;

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

    public HttpAsyncNotif(SelectOwner caller, boolean notifySelectedOwner){
        this.selectOwnerCaller = caller;
        this.notifySelectedOwner = notifySelectedOwner;
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
        if(selectOwnerCaller != null && !notifySelectedOwner)
            selectOwnerCaller.setProgressDialog(ProgressDialog.show(selectOwnerCaller, "Please wait...", "Search agreed users..."));
        if(selectOwnerCaller != null && notifySelectedOwner)
            selectOwnerCaller.setProgressDialog(ProgressDialog.show(selectOwnerCaller, "Please wait...", "Notifying owner..."));
    }

    @Override
    protected JSONArray doInBackground(Action... params) {
        if(params[0].equals(Action.SAVE_REQUEST))
            return saveRequest();
        if(params[0].equals(Action.GET_NOTIFS))
            return getNotification();
        if(params[0].equals(Action.UPDATE_REQUEST_STATE))
            return updateRequestSate();
        if(params[0].equals(Action.GET_REQUEST_BY_DATE))
            return getRequestByDate();
        if(params[0].equals(Action.GET_REQUEST_DATA))
            return getRequestData();
        if(params[0].equals(Action.GET_AGREED_OWNERS))
            return getAgreedUsers();
        if(params[0].equals(Action.NOTIFY_SELECTED_ONWER))
            return notifySelectedUser();
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
        if(selectOwnerCaller != null && !notifySelectedOwner){
            selectOwnerCaller.getProgressDialog().dismiss();
            selectOwnerCaller.onOnPostAgreedOnwers(jsonArray);
        }
        if(selectOwnerCaller != null && notifySelectedOwner){
            selectOwnerCaller.getProgressDialog().dismiss();
            selectOwnerCaller.onPostNotifySelectedOwner(jsonArray);
        }
    }

    private JSONArray getRequestByDate(){
        return new JsonParser().getRequestsByDate(tabOperationsCaller.getUser().getUsername());
    }

    private JSONArray getRequestData(){
        return new JsonParser().getRequestData(requestDataCaller.getUser().getUsername(), requestDataCaller.getFromDate(), requestDataCaller.getToDate(), Constants.GET_REQUEST_DATA_URL);
    }

    private JSONArray getAgreedUsers(){
       return new JsonParser().getAgreedOwners(selectOwnerCaller.getUser().getUsername(), selectOwnerCaller.getFromDate(), selectOwnerCaller.getToDate());
    }

    private JSONArray notifySelectedUser(){
        return new JsonParser().notifySelectedUser(selectOwnerCaller.getUser().getUsername(), selectOwnerCaller.getSelectedOwner().get("ownerName"),
                selectOwnerCaller.getSelectedOwner().get("brand"),
                selectOwnerCaller.getSelectedOwner().get("model"),
                selectOwnerCaller.getFromDate(), selectOwnerCaller.getToDate());
    }

    private JSONArray updateRequestSate(){
        boolean rentConfirmed = false;
        if(customRequestReceivedCaller.getChoice().equals(Action.CONFIRM_RENT))
            rentConfirmed = true;
        else if(customRequestReceivedCaller.getChoice().equals(Action.REFUTE_RENT.toString()))
            rentConfirmed = false;
        return new JsonParser().updateRequestState(Integer.valueOf(customRequestReceivedCaller.getCurrentNotificationId()), rentConfirmed, Constants.UPDATE_REQUEST_URL);
    }

    private JSONArray saveRequest(){
        // List<HashMaps<String, String> is like :
        // 0 -> "owner":"celien", "brand":"bmw", "model":"335i"
        return new JsonParser().saveRequest(
                listSpecificCarsCaller.getSelectedItems(),
                listSpecificCarsCaller.getUser().getUsername(),
                listSpecificCarsCaller.getDateFrom().toString(),
                listSpecificCarsCaller.getDateTo().toString(),
                listSpecificCarsCaller.isExchange(),
                listSpecificCarsCaller.getIdSelectedCar(),
                listSpecificCarsCaller.getMileage());
    }

    // Username and String to say that we have or not take into account that the notification has been read or not.
    private JSONArray getNotification(){
        return new JsonParser().getNotifications(requestReceivedCaller.getUser().getUsername(), Constants.GET_NOTIFS_URL, String.valueOf(requestReceivedCaller.haveToBeAlreadyRead()));
    }
}
