package com.example.celien.drivemycar.tabs;

import android.app.Activity;
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
import com.example.celien.drivemycar.fragment.ConfirmRent;
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

    private HashMap<String, String> notificationData;

    public static final int ID_FRAGMENT = 1244; // Random

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_operations, container, false);

        // Get the notification data from SharedPref and test is there is indeed something.
        this.notificationData = Tools.getNotificationData(getActivity().getSharedPreferences("notifInfo", Context.MODE_PRIVATE));
        if(notificationData.get("userSource").equals(""))
            Log.e(getClass().getName(), "There is nothing in SharedPref.notifInfo");
        else{
            init(rootView);
            setListeners();
        }

        return rootView;
    }

    private void init(View v){

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
        tvUserSource.setText(notificationData.get("userSource"));
        tvBrand.setText(notificationData.get("brand"));
        tvModel.setText(notificationData.get("model"));
        tvFromDate.setText(notificationData.get("fromDate").substring(0, 9));
        tvFromTime.setText(notificationData.get("fromDate").substring(10, notificationData.get("fromDate").length() - 5)+ " h");
        tvToDate.setText(notificationData.get("toDate").substring(0, 9));
        tvToTime.setText(notificationData.get("toDate").substring(10, notificationData.get("toDate").length() - 5)+ " h");
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
                    // Retrieve data from DatePicker
                    Bundle bdl  = data.getExtras();
                    double mileage = bdl.getDouble("mileage");
                    Toast.makeText(this.getActivity(), "Mileage received = " +mileage, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
