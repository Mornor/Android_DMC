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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomFragmentTabOperations;
import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.fragment.ConfirmRent;
import com.example.celien.drivemycar.http.HttpAsync;
import com.example.celien.drivemycar.http.HttpAsyncNotif;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private ProgressDialog progressDialog;
    private JSONArray notifications;
    private static boolean notificationInitialized;

    // ListView related stuff
    private ListAdapter adapter;
    private ListView lv;

    public static final int ID_FRAGMENT = 1244; // Random

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_operations, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View v){

        // Get the current user and the notifications if there is some.
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
        lv           = (ListView)v.findViewById(R.id.lvRequests);

        // Get the notifications in DB only if has not already been initialized
        Log.d("Message", String.valueOf(notificationInitialized));
        if(!notificationInitialized){
            new HttpAsyncNotif(this).execute(Action.GET_NOTIFS.toString(), user.getUsername(), "true");
            Log.d("Message", "Has been sent");
        }
    }

    public void onPostExecuteLoadNotification(JSONArray array){
        notifications = array;
        notificationInitialized = true;
        createListView(array);
    }

    private void createListView(JSONArray array){
        List<JSONObject> list = new ArrayList<>();
        try {
            for(int i = 0 ; i < array.length() ; i++){
                JSONObject temp = array.getJSONObject(i);
                list.add(temp);
            }
        } catch (JSONException e) {
            Log.e(e.getClass().getName(),"JSONException", e);
        }

        adapter = new CustomFragmentTabOperations(this.getActivity(), list, this);
        lv.setAdapter(adapter);

        setListeners();
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
        Toast.makeText(this.getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this.getActivity(), "Confirm", Toast.LENGTH_SHORT).show();
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
