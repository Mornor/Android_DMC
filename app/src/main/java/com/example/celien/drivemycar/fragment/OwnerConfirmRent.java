package com.example.celien.drivemycar.fragment;

import android.app. Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.http.HttpAsyncTransaction;
import com.example.celien.drivemycar.utils.Action;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OwnerConfirmRent extends DialogFragment{

    private EditText etMileage;
    private Button btnConfirm;
    private Button btnCancel;
    private String brand;
    private String model;
    private String fromDate;
    private String toDate;
    private String idTransaction;
    private String driverName;
    private ProgressDialog progressDialog;
    private String mileage;


    @NonNull
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
                JSONObject objectReceived = new JSONObject(rcvd.getString("json"));
                brand           = objectReceived.getString("brand");
                model           = objectReceived.getString("model");
                fromDate        = objectReceived.getString("fromDate");
                toDate          = objectReceived.getString("toDate");
                driverName      = objectReceived.getString("driverName");
                idTransaction   = objectReceived.getString("id");
            } catch (JSONException e) {
                Log.e(e.getClass().getName(), "JSONException", e);
            }

        etMileage               = (EditText)v.findViewById(R.id.etSetOdometer);
        TextView tvSetOdometer  = (TextView) v.findViewById(R.id.tvSetOdometer);
        btnConfirm              = (Button)v.findViewById(R.id.btnOk);
        btnCancel               = (Button)v.findViewById(R.id.btnCancel);

        tvSetOdometer.setText("Before the rent of your " + brand + " " + model + " to " + driverName + " from " + fromDate.substring(0, 10) + " to " + toDate.substring(0, 10) + ", please indicate the current mileage below (KMs):");
    }

    private void setListeners(){
        btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendOdometerValue();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
    }

    public void onPostExecute(JSONArray array){
        try{
            if(array.getJSONObject(0).getBoolean("success")){
                Toast.makeText(this.getActivity(), "Odometer value set!", Toast.LENGTH_SHORT).show();
                dismiss();

            }
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }
    }

    private void dismissDialog(){
        dismiss();
    }

    private void sendOdometerValue(){
        mileage = etMileage.getText().toString();
        new HttpAsyncTransaction(this).execute(Action.SET_ODOMETER);
    }

    /*Getters and Setters*/
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public String getMileage() {
        return mileage;
    }

    public boolean isOwner() {
        return true;
    }

    public String getIdTransaction() {
        return idTransaction;
    }

    public String getNullValueOfOdometer(){
        return "0.0";
    }
}
