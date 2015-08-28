package be.iba.carswop.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import be.iba.carswop.R;
import be.iba.carswop.http.HttpAsyncTransaction;
import be.iba.carswop.utils.Action;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequesterConfirmRent extends DialogFragment {

    private EditText etSetConso;
    private EditText etSetMileage;
    private ProgressDialog progressDialog;
    private EditText etNbFilledTanks;
    private EditText etExtraKms;
    private Switch ownCard;

    private String brand;
    private boolean ownCardBoolean;
    private String model;
    private String mileage;
    private String conso;
    private int nbFilledTanks;
    private int nbExtraKms;
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
                JSONObject transaction = new JSONObject(rcvd.getString("json"));
                brand           = transaction.getString("brand");
                model           = transaction.getString("model");
                ownerName       = transaction.getString("ownerName");
                idTransaction   = transaction.getInt("id");
                startMileage    = transaction.getDouble("startMileage");
            } catch (JSONException e) {
                Log.e(e.getClass().getName(), "JSONException", e);
            }

        // Get the items from the Layout
        TextView tvSetConso     = (TextView) v.findViewById(R.id.tvSetConso);
        TextView tvSetMileage   = (TextView) v.findViewById(R.id.tvSetOdometer);
        etSetConso              = (EditText)v.findViewById(R.id.etSetConso);
        etSetMileage            = (EditText)v.findViewById(R.id.etSetOdometer);
        etNbFilledTanks         = (EditText)v.findViewById(R.id.etNbTankFilled);
        ownCard                 = (Switch)v.findViewById(R.id.switch1);
        etExtraKms              = (EditText)v.findViewById(R.id.etExtraKms);
        Button btnOk            = (Button) v.findViewById(R.id.btnOk);
        Button btnCancel        = (Button) v.findViewById(R.id.btnCancel);

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

        ownCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ownCardBoolean = b;
            }
        });
    }

    private void setOdometer(){
        if(etSetMileage.getText().toString().isEmpty() || etSetConso.getText().toString().isEmpty())
            Toast.makeText(this.getActivity(), "Please set the value of the fields", Toast.LENGTH_SHORT).show();
        else{
            mileage = etSetMileage.getText().toString();
            conso   = etSetConso.getText().toString();
            new HttpAsyncTransaction(this, false).execute(Action.SET_ODOMETER);
        }
    }

    public void onPostSetOdometer(JSONArray array){
        try{
            if(array.getJSONObject(0).getBoolean("success")) {
                Toast.makeText(getActivity(), "Odometer is successfully set", Toast.LENGTH_SHORT).show();
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

        Toast.makeText(this.getActivity(), "You have to pay "+amountToPay+"e to " +ownerName, Toast.LENGTH_LONG).show();

        dismissDialog();
    }


    private void getAmountToPay(){
        nbFilledTanks   = Integer.valueOf(etNbFilledTanks.getText().toString());
        nbExtraKms      = Integer.valueOf(etExtraKms.getText().toString());
        new HttpAsyncTransaction(this, true).execute(Action.COMPUTE_AMOUNT_TO_PAY);
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

    public String getMileage() {
        return mileage;
    }

    public String getIdTransaction() {
        return String.valueOf(idTransaction);
    }

    public String getConso() {
        return conso;
    }

    public int getNbExtraKms() {
        return nbExtraKms;
    }

    public void setNbExtraKms(int nbExtraKms) {
        this.nbExtraKms = nbExtraKms;
    }

    public int getNbFilledTanks() {
        return nbFilledTanks;
    }

    public void setNbFilledTanks(int nbFilledTanks) {
        this.nbFilledTanks = nbFilledTanks;
    }

    public void setConso(String conso) {
        this.conso = conso;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public double getStartMileage() {
        return startMileage;
    }

    public void setStartMileage(double startMileage) {
        this.startMileage = startMileage;
    }

    public void setIdTransaction(int idTransaction) {
        this.idTransaction = idTransaction;
    }

    public boolean isOwnCardBoolean() {
        return ownCardBoolean;
    }

    // "false" is true if it is the owner who set the value at the BEGINNING of the Transaction (false if it is the driver)
    public boolean isOwner(){
        return false;
    }


}
