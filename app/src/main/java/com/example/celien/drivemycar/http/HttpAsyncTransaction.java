package com.example.celien.drivemycar.http;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.example.celien.drivemycar.core.AcceptedRequest;
import com.example.celien.drivemycar.fragment.ConfirmRent;
import com.example.celien.drivemycar.tabs.TabOperations;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Constants;

import org.json.JSONArray;

public class HttpAsyncTransaction extends AsyncTask<String, Void, JSONArray> {

    private AcceptedRequest acceptedRequestCaller;
    private ConfirmRent confirmRentCaller;
    private FragmentActivity activityFromOperationsCaller;
    private TabOperations tabOperationsCaller;

    public HttpAsyncTransaction(AcceptedRequest caller){
        this.acceptedRequestCaller = caller;
    }

    public HttpAsyncTransaction(ConfirmRent caller){
        this.confirmRentCaller = caller;
    }

    public HttpAsyncTransaction(FragmentActivity activity, TabOperations caller){
        this.activityFromOperationsCaller = activity;
        this.tabOperationsCaller = caller;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(acceptedRequestCaller != null)
            acceptedRequestCaller.setProgressDialog(ProgressDialog.show(acceptedRequestCaller, "Please wait...", "Search transaction"));
        if(confirmRentCaller != null)
            confirmRentCaller.setProgressDialog(ProgressDialog.show(confirmRentCaller.getActivity(), "Please wait...", "Set odometer..."));
        if(tabOperationsCaller != null)
            tabOperationsCaller.setProgressDialog(ProgressDialog.show(tabOperationsCaller.getActivity(), "Please wait...", "Check status of transaction..."));
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        if(params[0].equals(Action.GET_TRANSACTIONS.toString()))
            return getTransactions(params[1]); // username
        if(params[0].equals(Action.SET_ODOMETER.toString()))
            return setOdometer(params[1], params[2]);  // Mileage and id Transaction;
        if(params[0].equals(Action.CHECK_TRANSACTIION_STATUS.toString()))
            return checkTransactionStatus(params[1], params[2], params[3]); // username, fromDate, toDate
        return null;
    }

    @Override
    protected void onPostExecute(JSONArray array) {
        super.onPostExecute(array);
        if(acceptedRequestCaller != null){
            acceptedRequestCaller.getProgressDialog().dismiss();
            acceptedRequestCaller.onPostExecuteGetTransaction(array);
        }
        if(confirmRentCaller != null){
            confirmRentCaller.getProgressDialog().dismiss();
            confirmRentCaller.onPostExecute(array);
        }
        if(activityFromOperationsCaller != null){
            tabOperationsCaller.getProgressDialog().dismiss();
            tabOperationsCaller.onPostCheckTransactionStatus(array);
        }
    }

    private JSONArray checkTransactionStatus(String username, String fromDate, String toDate){
        return new JsonParser().getRequestData(username, fromDate, toDate, Constants.GET_TRANSACTION_STATE_URL);
    }

    private JSONArray setOdometer(String mileage, String idTransaction){
        return new JsonParser().setOdometer(mileage, idTransaction);
    }

    private JSONArray getTransactions(String username){
        return new JsonParser().getTransactions(username);
    }
}
