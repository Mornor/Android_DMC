package com.example.celien.drivemycar.http;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import com.example.celien.drivemycar.core.AcceptedRequest;
import com.example.celien.drivemycar.fragment.OwnerConfirmRent;
import com.example.celien.drivemycar.fragment.RequesterConfirmRent;
import com.example.celien.drivemycar.tabs.TabOperations;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Constants;
import org.json.JSONArray;

public class HttpAsyncTransaction extends AsyncTask<Action, Void, JSONArray> {

    private AcceptedRequest acceptedRequestCaller;
    private OwnerConfirmRent ownerConfirmRentCaller;
    private FragmentActivity activityFromOperationsCaller;
    private TabOperations tabOperationsCaller;
    private RequesterConfirmRent requesterConfirmRentCaller;
    private boolean isPaiementRequested; // If true, then launch a request into DB to compute the amount to pay;

    public HttpAsyncTransaction(AcceptedRequest caller){
        this.acceptedRequestCaller = caller;
    }

    public HttpAsyncTransaction(OwnerConfirmRent caller){
        this.ownerConfirmRentCaller = caller;
    }

    public HttpAsyncTransaction(FragmentActivity activity, TabOperations caller){
        this.activityFromOperationsCaller = activity;
        this.tabOperationsCaller = caller;
    }

    public HttpAsyncTransaction(RequesterConfirmRent caller, boolean isPaiementRequested){
        this.requesterConfirmRentCaller = caller;
        this.isPaiementRequested = isPaiementRequested;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(acceptedRequestCaller != null)
            acceptedRequestCaller.setProgressDialog(ProgressDialog.show(acceptedRequestCaller, "Please wait...", "Search transaction"));
        if(ownerConfirmRentCaller != null)
            ownerConfirmRentCaller.setProgressDialog(ProgressDialog.show(ownerConfirmRentCaller.getActivity(), "Please wait...", "Set odometer..."));
        if(tabOperationsCaller != null)
            tabOperationsCaller.setProgressDialog(ProgressDialog.show(tabOperationsCaller.getActivity(), "Please wait...", "Check status of transaction..."));
        if(requesterConfirmRentCaller != null && !isPaiementRequested)
            requesterConfirmRentCaller.setProgressDialog(ProgressDialog.show(requesterConfirmRentCaller.getActivity(), "Please wait...", "Set the odometer value..."));
        if(requesterConfirmRentCaller != null && isPaiementRequested)
            requesterConfirmRentCaller.setProgressDialog(ProgressDialog.show(requesterConfirmRentCaller.getActivity(), "Please wait...", "Compute amount to pay..."));
    }

    @Override
    protected JSONArray doInBackground(Action... params) {
        if(params[0].equals(Action.GET_TRANSACTIONS))
            return getTransactions();
        if(params[0].equals(Action.SET_ODOMETER))
            return setOdometer();
        if(params[0].equals(Action.CHECK_TRANSACTION_STATUS))
            return checkTransactionStatus();
        if(params[0].equals(Action.COMPUTE_AMOUNT_TO_PAY))
            return computeAmountToPay(); // idTransaction
        return null;
    }

    @Override
    protected void onPostExecute(JSONArray array) {
        super.onPostExecute(array);
        if(acceptedRequestCaller != null){
            acceptedRequestCaller.getProgressDialog().dismiss();
            acceptedRequestCaller.onPostExecuteGetTransaction(array);
        }
        if(ownerConfirmRentCaller != null){
            ownerConfirmRentCaller.getProgressDialog().dismiss();
            ownerConfirmRentCaller.onPostExecute(array);
        }
        if(activityFromOperationsCaller != null){
            tabOperationsCaller.getProgressDialog().dismiss();
            tabOperationsCaller.onPostCheckTransactionStatus(array);
        }
        if(requesterConfirmRentCaller != null && !isPaiementRequested){
            requesterConfirmRentCaller.getProgressDialog().dismiss();
            requesterConfirmRentCaller.onPostSetOdometer(array);
        }
        if(requesterConfirmRentCaller != null && isPaiementRequested){
            requesterConfirmRentCaller.getProgressDialog().dismiss();
            requesterConfirmRentCaller.onPostComputeAmountToPay(array);
        }
    }

    private JSONArray computeAmountToPay(){
        return new JsonParser().computeAmountToPay(requesterConfirmRentCaller.getIdTransaction());
    }

    private JSONArray checkTransactionStatus(){
        return new JsonParser().getRequestData(tabOperationsCaller.getUser().getUsername(), tabOperationsCaller.getFromDate(), tabOperationsCaller.getToDate(), Constants.GET_TRANSACTION_STATE_URL);
    }

    private JSONArray setOdometer(){
        if(ownerConfirmRentCaller != null)
            return new JsonParser().setOdometer(ownerConfirmRentCaller.getMileage(), ownerConfirmRentCaller.getIdTransaction(), String.valueOf(ownerConfirmRentCaller.isOwner()), ownerConfirmRentCaller.getNullValueOfOdometer());
        else
            return new JsonParser().setOdometer(requesterConfirmRentCaller.getMileage(), requesterConfirmRentCaller.getIdTransaction(), String.valueOf(requesterConfirmRentCaller.isOwner()), requesterConfirmRentCaller.getConso());
    }

    private JSONArray getTransactions(){
        return new JsonParser().getTransactions(acceptedRequestCaller.getUser().getUsername());
    }
}
