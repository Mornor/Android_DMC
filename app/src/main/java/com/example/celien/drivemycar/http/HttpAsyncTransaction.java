package com.example.celien.drivemycar.http;


import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.celien.drivemycar.core.AcceptedRequest;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;

public class HttpAsyncTransaction extends AsyncTask<String, Void, JSONArray> {

    private AcceptedRequest acceptedRequestCaller;

    public HttpAsyncTransaction(AcceptedRequest caller){
        this.acceptedRequestCaller = caller;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(acceptedRequestCaller != null)
            acceptedRequestCaller.setProgressDialog(ProgressDialog.show(acceptedRequestCaller, "Please wait...", "Search transaction"));
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        if(params[0].equals(Action.GET_TRANSACTIONS.toString()))
            return getTransactions(params[1]); // username
        return null;
    }

    @Override
    protected void onPostExecute(JSONArray array) {
        super.onPostExecute(array);
        if(acceptedRequestCaller != null){
            acceptedRequestCaller.getProgressDialog().dismiss();
            acceptedRequestCaller.onPostExecuteGetTransaction(array);
        }
    }

    private JSONArray getTransactions(String username){
        return new JsonParser().getTransactions(username);
    }
}
