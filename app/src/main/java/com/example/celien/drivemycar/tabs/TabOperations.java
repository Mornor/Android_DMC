package com.example.celien.drivemycar.tabs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomTabOperation;
import com.example.celien.drivemycar.core.AcceptedRequest;
import com.example.celien.drivemycar.core.RequestData;
import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.core.RequestReceived;
import com.example.celien.drivemycar.fragment.RequesterConfirmRent;
import com.example.celien.drivemycar.http.HttpAsyncNotif;
import com.example.celien.drivemycar.http.HttpAsyncTransaction;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.NotificationTypeConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TabOperations extends Fragment {

    private User user;

    private ListAdapter adapter;
    private Button btnRequests;
    private Button btnRequestAccepted;
    private ListView lvRequestStatus;
    private ProgressDialog progressDialog;
    private View rootView;

    private String fromDate;
    private String toDate;

    private JSONObject object;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_operations, container, false);
        init(rootView);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisible())
            loadUserRequestByDate();
    }

    private void init(View v){
        // Retrieve the user from tab hosting (Home)
        Home homeActivity   = (Home)getActivity();
        user                = homeActivity.getUser();

        btnRequests         = (Button)v.findViewById(R.id.btnCheckRequests);
        btnRequestAccepted  = (Button)v.findViewById(R.id.btnCheckRequestAccepted);
        lvRequestStatus     = (ListView)v.findViewById(R.id.lvRequestsStatut);

        // Set the listeners
        btnRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lauchIntentToRequestReceived();
            }
        });

        btnRequestAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntentToRequestAccepted();
            }
        });
    }

    private void loadUserRequestByDate(){
        if(user != null)
            new HttpAsyncNotif(getActivity(), this).execute(Action.GET_REQUEST_BY_DATE);
    }

    private void launchIntentToRequestAccepted(){
        Intent i = new Intent(this.getActivity(), AcceptedRequest.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void onPostExecuteLoadRequestByDate(JSONArray array){
        List<JSONObject> list = new ArrayList<>();
        try {

            if(!array.getJSONObject(0).getBoolean("success"))
                Log.e("Error", "JSON empty");
            else {
                // Start from 1 because 0 is the JSON to indicate if array is empty (true) or not
                for (int i = 1; i < array.length(); i++) {
                    JSONObject temp = array.getJSONObject(i);
                    list.add(temp);
                }
            }
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        adapter = new CustomTabOperation(this.getActivity(), list);
        lvRequestStatus.setAdapter(adapter);

        // Set listener to ListView
        lvRequestStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject jsonObjectClicked = (JSONObject) parent.getItemAtPosition(position);
                launchNextStep(jsonObjectClicked);
            }
        });
    }

    public void onPostCheckTransactionStatus(JSONArray array){
        String transactionStatus = "";
        JSONObject transactionData = new JSONObject();
        try{
            if(!array.getJSONObject(0).getBoolean("success"))
                Log.e("Error", "There is no such transaction in DB");
            else{
                transactionData = array.getJSONObject(1);
                transactionStatus = array.getJSONObject(1).getString("status");
            }

        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        if(transactionStatus.equals(NotificationTypeConstants.ONWER_SET_ODOMETER)){
            RequesterConfirmRent cr = new RequesterConfirmRent();
            Bundle bdl = new Bundle();
            bdl.putString("json", transactionData.toString());
            cr.setArguments(bdl);
            cr.show(getFragmentManager(), "");
        }
        else
            launchIntentToRequestData();
    }

    private void launchIntentToRequestData(){
        Intent i = new Intent(this.getActivity(), RequestData.class);
        Bundle bdl = new Bundle();
        bdl.putParcelable("user", user);
        bdl.putString("json", object.toString()); // Have to pass the JSON as a String because no implemented methods.
        i.putExtras(bdl);
        startActivity(i);
    }

    /** Next step is either show some request data or to set the odomoter (when the car has been driven)
     * To know which step has to be launched, we have to query the DB to check if the transaction exist or not
     * If it exist, next step is to set the odometer
     * If not, next step is to show the request data */
    private void launchNextStep(JSONObject object){
        this.object = object;
        try{
            fromDate = object.getString("fromDate");
            toDate   = object.getString("toDate");
        }catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        new HttpAsyncTransaction(this.getActivity(), this).execute(Action.CHECK_TRANSACTION_STATUS);
    }

    private void lauchIntentToRequestReceived(){
        Intent i = new Intent(this.getActivity(), RequestReceived.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);
        startActivity(i);
    }

    /*Getters and Setters*/
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public User getUser() {
        return user;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }
}
