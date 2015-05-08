package com.example.celien.drivemycar.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.celien.drivemycar.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RequesterConfirmRent extends DialogFragment {

    private TextView tvSetMileage;
    private TextView tvSetConso;
    private EditText etSetConso;
    private EditText etSetMileage;
    private Button btnOk;
    private Button btnCancel;

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
        tvSetConso.setText("Please, set the final mileage of the " +brand+ " " +model+ " of " +ownerName+". " +
                "Just to remind you, the car had "
                +startMileage+ " kms when you took it");
        tvSetMileage.setText("Indicate below the average consumption of fuel");

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

    }

    private void dismissDialog(){
        dismiss();
    }
}
