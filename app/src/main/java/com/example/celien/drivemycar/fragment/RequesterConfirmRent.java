package com.example.celien.drivemycar.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.http.HttpAsyncTransaction;
import com.example.celien.drivemycar.http.JsonParser;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequesterConfirmRent extends DialogFragment {

    private TextView tvSetMileage;
    private TextView tvSetConso;
    private EditText etSetConso;
    private EditText etSetMileage;
    private Button btnOk;
    private Button btnCancel;
    private ProgressDialog progressDialog;

    // Some useful data from the Transaction Json;
    private JSONObject transaction; // Contains data of the current Transaction.
    private String brand;
    private String model;
    private double startMileage;
    private String ownerName;
    private int idTransaction;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Set the final mileage");
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_requester_confirm_rent, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View v){

        // Get the Transaction Data from TabOperations
        Bundle rcvd = getArguments();
        if(rcvd != null)
            try {
                transaction     = new JSONObject(rcvd.getString("json"));
                brand           = transaction.getString("brand");
                model           = transaction.getString("model");
                ownerName       = transaction.getString("ownerName");
                idTransaction   = transaction.getInt("id");
                startMileage    = transaction.getDouble("startMileage");
            } catch (JSONException e) {
                Log.e(e.getClass().getName(), "JSONException", e);
            }

        // Get the items from the Layout
        tvSetConso      = (TextView)v.findViewById(R.id.tvSetConso);
        tvSetMileage    = (TextView)v.findViewById(R.id.tvSetOdometer);
        etSetConso      = (EditText)v.findViewById(R.id.etSetConso);
        etSetMileage    = (EditText)v.findViewById(R.id.etSetOdometer);
        btnOk           = (Button)v.findViewById(R.id.btnOk);
        btnCancel       = (Button)v.findViewById(R.id.btnCancel);

        // Set value of TextView based on Json Received
        tvSetMileage.setText("Please, set the final mileage of the " + brand + " " + model + " of " + ownerName + ". " +
                "Just to remind you, the car had "
                + startMileage + " kms when you took it");
        tvSetConso.setText("Indicate below the average consumption of fuel");

        // Set the listeners
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOdometer();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
    }

    private void setOdometer(){
        if(etSetMileage.getText().toString().isEmpty() || etSetConso.getText().toString().isEmpty())
            Toast.makeText(this.getActivity(), "Please set the value of the fields", Toast.LENGTH_SHORT).show();
        else{
            // "false" in execute parameter is true if it is the owner who set the value at the BEGINNING of the Transaction (false if it is the driver)
            new HttpAsyncTransaction(this, false).execute(Action.SET_ODOMETER.toString(), etSetMileage.getText().toString(), String.valueOf(idTransaction), "false", etSetConso.getText().toString());
        }
    }

    public void onPostSetOdometer(JSONArray array){
        try{
            if(array.getJSONObject(0).getBoolean("success")) {
                Toast.makeText(getActivity(), "Odometer is successfully set", Toast.LENGTH_SHORT).show();
                dismissDialog();
                getAmountToPay();
            }
            else
                Toast.makeText(getActivity(), "Error, please try again", Toast.LENGTH_SHORT).show();
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }
    }

    public void onPostComputeAmountToPay(JSONArray array){
        double amountToPay = 0.0;
        String ownerName = "";
        try{
            if(!array.getJSONObject(0).getBoolean("success"))
                Log.e("Error", "Error with JSON received");
            else {
                amountToPay = array.getJSONObject(1).getDouble("amountToPay");
                ownerName   = array.getJSONObject(2).getString("ownerName");
            }
        }catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        Log.d("Rcvd ", String.valueOf(amountToPay));
        Log.d("Rcvd ", ownerName);

        // Toast.makeText(this.getActivity(), "You have to pay "+amountToPay+"e to " +ownerName, Toast.LENGTH_LONG).show();

        if(getActivity() == null)
            Log.d("Rcvd ", "Act is null");

        /*
        Paiement p = new Paiement();
        Bundle bdl = new Bundle();
        bdl.putString("ownerName", ownerName);
        bdl.putDouble("amountToPay", amountToPay);
        p.setArguments(bdl);
        // BUGGY LINE
       FragmentManager f = getActivity().getFragmentManager();
        if(p == null)
            Log.d("Exception ", "p is null");
        if(f == null)
            Log.d("Exception ", "f is null");

        try {
            p.show(f, "4554");
        }catch(NullPointerException e){
            Log.d("Exception  ", e.toString());
        }*/

    }

    private class GetAmountToPay extends AsyncTask<String, Void, JSONArray>{

        private RequesterConfirmRent requesterConfirmRentCaller;

        public GetAmountToPay(RequesterConfirmRent caller){
            this.requesterConfirmRentCaller = caller;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(requesterConfirmRentCaller != null)
                requesterConfirmRentCaller.setProgressDialog(ProgressDialog.show(requesterConfirmRentCaller.getActivity(), "Please wait...", "Compute amount to pay..."));
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            if(params[0].equals(Action.COMPUTE_AMOUNT_TO_PAY.toString()))
                return computeAmountToPay(params[1]); // idTransaction
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            if(requesterConfirmRentCaller != null){
                requesterConfirmRentCaller.onPostComputeAmountToPay(array);
            }
        }

        private JSONArray computeAmountToPay(String idTransaction){
            return new JsonParser().computeAmountToPay(idTransaction);
        }
    }

    private void getAmountToPay(){
        //new HttpAsyncTransaction(this, true).execute(Action.COMPUTE_AMOUNT_TO_PAY.toString(), String.valueOf(idTransaction));
        new GetAmountToPay(this).execute(Action.COMPUTE_AMOUNT_TO_PAY.toString(), String.valueOf(idTransaction));
    }

    private void dismissDialog(){
        dismiss();
    }

    /*Getters and Setter*/
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
}
