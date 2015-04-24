package com.example.celien.drivemycar.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.fragment.ConfirmRent;
import com.example.celien.drivemycar.tabs.TabOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CustomFragmentTabOperations extends ArrayAdapter<JSONObject> {

    private JSONObject currentJson;
    private TabOperations caller;
    private TextView tvUserSource;
    private TextView tvBrand;
    private TextView tvModel;
    private TextView tvFromDate;
    private TextView tvToDate;
    private Button btnValidate;
    private Button btnCancel;
    public static final int ID_FRAGMENT = 1244; // Random


    public CustomFragmentTabOperations(Context context, List<JSONObject> list, TabOperations caller) {
        super(context, R.layout.custom_fragment_tab_operations, list);
        this.caller = caller;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_fragment_tab_operations, parent, false);

        // Get the elements on the layout
        currentJson  = getItem(position);
        tvUserSource = (TextView)v.findViewById(R.id.tvUserSource);
        tvBrand      = (TextView)v.findViewById(R.id.tvBrand);
        tvModel      = (TextView)v.findViewById(R.id.tvModel);
        tvFromDate   = (TextView)v.findViewById(R.id.tvDateFrom);
        tvToDate     = (TextView)v.findViewById(R.id.tvDateTo);
        btnValidate  = (Button)v.findViewById(R.id.btnValidate);
        btnCancel    = (Button)v.findViewById(R.id.btnCancel);

        // Set the value of the fields
        try {
            tvUserSource.setText(currentJson.getString("userSource"));
            tvBrand.setText(currentJson.getString("brand"));
            tvModel.setText(currentJson.getString("model"));
            tvFromDate.setText(currentJson.getString("fromDate").substring(0, 9)+" at" +currentJson.getString("fromDate").substring(10, currentJson.getString("fromDate").toString().length() - 5)+ "h ");
            tvToDate.setText(currentJson.getString("toDate").substring(0, 9)+" at" +currentJson.getString("toDate").substring(10, currentJson.getString("toDate").toString().length() - 5)+ "h ");
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        // Set the button listeners
        btnValidate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickValidate();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickCancel();
            }
        });

        return v;
    }

    private void onClickValidate(){
        ConfirmRent confirmRent = new ConfirmRent();
        //confirmRent.setTargetFragment(this, ID_FRAGMENT);
        //confirmRent.show(this.getContext().getFragmentManager(), "validate");
    }

    private void onClickCancel(){
        Toast.makeText(this.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
    }

    // Retrieve the data from fragment.ConfirmRent which is called when user click on button "for sure"
    public void onActivityResult(int reqCode, int resCode, Intent data){
        switch (reqCode){
            case ID_FRAGMENT :
                if(resCode == Activity.RESULT_OK){
                    // Retrieve data from fragment.ConfirmRent
                    Bundle bdl  = data.getExtras();
                    double mileage = bdl.getDouble("mileage");
                    sendConfirmRequest(mileage);
                }
                break;
            default:
                break;
        }
    }

    private void sendConfirmRequest(double mileage){
        Toast.makeText(this.getContext(), "Confirm", Toast.LENGTH_SHORT).show();
    }

    public void onPostExecuteConfirmRent(int responseStatus){
        Log.d("Response from server ", String.valueOf(responseStatus));
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public JSONObject getItem(int position) {
        return super.getItem(position);
    }
}
