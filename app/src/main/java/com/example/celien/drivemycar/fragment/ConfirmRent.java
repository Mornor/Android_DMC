package com.example.celien.drivemycar.fragment;

import android.app.Activity;
import android.app. Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.celien.drivemycar.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class ConfirmRent extends DialogFragment{

    private EditText etMileage;
    private TextView tvSetOdometer;
    private Button btnConfirm;
    private Button btnCancel;
    private JSONObject objectReceived;
    private String brand;
    private String model;
    private String fromDate;
    private String toDate;
    private String driverName;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Confirm rent of your car");
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirm_rent, container, false);
        init(rootView);
        setListeners();
        return rootView;
    }

    private void init(View v){

        // Get the Bundle data from AcceptedRequest Activity.
        Bundle rcvd = getArguments();
        if(rcvd != null)
            try {
                objectReceived  = new JSONObject(rcvd.getString("json"));
                brand           = objectReceived.getString("brand");
                model           = objectReceived.getString("model");
                fromDate        = objectReceived.getString("fromDate");
                toDate          = objectReceived.getString("toDate");
                driverName      = objectReceived.getString("driverName");
                Log.d("Brand/model", brand +"/" +model);
            } catch (JSONException e) {
                Log.e(e.getClass().getName(), "JSONException", e);
            }

        etMileage       = (EditText)v.findViewById(R.id.etSetOdometer);
        tvSetOdometer   = (TextView)v.findViewById(R.id.tvSetOdometer);
        btnConfirm      = (Button)v.findViewById(R.id.btnOk);
        btnCancel       = (Button)v.findViewById(R.id.btnCancel);

        tvSetOdometer.setText("Before the rent of your " +brand+ " " +model+ " to " +driverName+ " from "+fromDate.substring(0,10)+ " to "+toDate.substring(0,10)+ ", please indicate the current mileage below (KMs):");
    }

    private void setListeners(){
        btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendOdometerValue();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
    }

    private void dismissDialog(){
        dismiss();
    }

    private void sendOdometerValue(){

        dismiss();
    }
}
