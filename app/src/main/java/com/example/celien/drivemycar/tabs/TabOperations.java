package com.example.celien.drivemycar.tabs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.fragment.ConfirmRent;
import com.example.celien.drivemycar.http.HttpAsync;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Tools;

import org.w3c.dom.Text;

import java.util.HashMap;

public class TabOperations extends Fragment {

    private TextView tvUserSource;
    private TextView tvBrand;
    private TextView tvModel;
    private TextView tvFromDate;
    private TextView tvFromTime;
    private TextView tvToDate;
    private TextView tvToTime;
    private Button btnValidate;
    private Button btnCancel;

    private User user;
    private HashMap<String, String> transactionData;
    private ProgressDialog progressDialog;

    public static final int ID_FRAGMENT = 1244; // Random

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_operations, container, false);

        // Get the notification data from SharedPref and test is there is indeed something.
        this.transactionData = Tools.getNotificationData(getActivity().getSharedPreferences("transactionData", Context.MODE_PRIVATE));
        if(transactionData.get("userSource").equals(""))
            Log.e(getClass().getName(), "There is nothing in SharedPref.notifInfo");
        else{
            init(rootView);
            setListeners();
        }

        return rootView;
    }

    private void init(View v){

        // Get the current user
        Home homeActivity = (Home)getActivity();
        user = homeActivity.getUser();

        // Get the elements on the layout
        tvUserSource = (TextView)v.findViewById(R.id.tvUserSource);
        tvBrand      = (TextView)v.findViewById(R.id.tvBrand);
        tvModel      = (TextView)v.findViewById(R.id.tvModel);
        tvFromDate   = (TextView)v.findViewById(R.id.tvDateFrom);
        tvFromTime   = (TextView)v.findViewById(R.id.tvTimeFrom);
        tvToDate     = (TextView)v.findViewById(R.id.tvDateTo);
        tvToTime     = (TextView)v.findViewById(R.id.tvTimeTo);
        btnValidate  = (Button)v.findViewById(R.id.btnValidate);
        btnCancel    = (Button)v.findViewById(R.id.btnCancel);

        // Init their values
        tvUserSource.setText(transactionData.get("userSource"));
        tvBrand.setText(transactionData.get("brand"));
        tvModel.setText(transactionData.get("model"));
        tvFromDate.setText(transactionData.get("fromDate").substring(0, 9));
        tvFromTime.setText(transactionData.get("fromDate").substring(10, transactionData.get("fromDate").length() - 5)+ " h");
        tvToDate.setText(transactionData.get("toDate").substring(0, 9));
        tvToTime.setText(transactionData.get("toDate").substring(10, transactionData.get("toDate").length() - 5)+ " h");
    }

    private void setListeners(){
        btnValidate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickValidate(v);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickCancel(v);
            }
        });
    }

    public void onClickValidate(View v){
        ConfirmRent confirmRent = new ConfirmRent();
        confirmRent.setTargetFragment(this, ID_FRAGMENT);
        confirmRent.show(getFragmentManager(), "validate");
    }

    public void onClickCancel(View v){
        Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
    }

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
        new HttpAsync(this).execute(Action.CONFIRM_RENT.toString(), String.valueOf(mileage), transactionData.get("id_transaction"));
    }

    public void onPostExecuteConfirmRent(int responseStatus){
        Log.d("Response from server ", String.valueOf(responseStatus));
    }

    /*Getters and Setter*/
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
}
