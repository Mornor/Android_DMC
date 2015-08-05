package be.iba.carswop.tabs;

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
import android.widget.TextView;
import android.widget.Toast;

import be.iba.carswop.R;
import be.iba.carswop.adapter.CustomTabOperation;
import be.iba.carswop.core.AcceptedRequest;
import be.iba.carswop.core.RequestData;
import be.iba.carswop.core.Home;
import be.iba.carswop.core.RequestReceived;
import be.iba.carswop.fragment.RequesterConfirmRent;
import be.iba.carswop.http.HttpAsyncNotif;
import be.iba.carswop.http.HttpAsyncTransaction;
import be.iba.carswop.models.User;
import be.iba.carswop.utils.Action;
import be.iba.carswop.utils.NotificationTypeConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class TabOperations extends Fragment {

    private User user;

    private ListView lvRequestStatus;
    private ProgressDialog progressDialog;

    private String fromDate;
    private String toDate;
    private TextView tvReceivedOrSent;
    private boolean isReceived; // True if yes.

    private JSONObject object;

    private boolean hasBeenDisplayed = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_operations, container, false);
        init(rootView);
        return rootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisible() && !hasBeenDisplayed)
            loadUserRequestByDate();
    }

    private void init(View v){
        // Retrieve the user from tab hosting (Home)
        Home homeActivity   = (Home)getActivity();
        user                = homeActivity.getUser();

        Button btnReceived          = (Button)v.findViewById(R.id.btnReceived);
        Button btnSent              = (Button)v.findViewById(R.id.btnSent);
        tvReceivedOrSent            = (TextView)v.findViewById(R.id.tvReceivedOrSent);
        lvRequestStatus             = (ListView)v.findViewById(R.id.lvRequestsStatut);

        // Set the listeners
        btnReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lauchIntentToRequestReceived();
                updateReceivedOrSent(true, "RECEIVED");
            }
        });

        btnSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launchIntentToRequestSent();
                updateReceivedOrSent(false, "SENT");
            }
        });
    }

    private void updateReceivedOrSent(boolean isReceived, String choice){
        this.isReceived = isReceived;
        tvReceivedOrSent.setText(choice);
    }

    private void loadUserRequestByDate(){
        if(user != null)
            new HttpAsyncNotif(getActivity(), this).execute(Action.GET_REQUEST_BY_DATE);
    }

    public void onPostExecuteLoadRequestByDate(JSONArray array){
        List<JSONObject> requestByDate = new ArrayList<>();
        try {

            if(!array.getJSONObject(0).getBoolean("success"))
                Log.e("Error", "JSON empty");
            else {
                // Start from 1 because 0 is the JSON to indicate if array is empty (true) or not
                for (int i = 1; i < array.length(); i++) {
                    JSONObject temp = array.getJSONObject(i);
                    requestByDate.add(temp);
                }
            }
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        ListAdapter adapter = new CustomTabOperation(this.getActivity(), requestByDate);
        lvRequestStatus.setAdapter(adapter);

        // Set listener to ListView
        lvRequestStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject jsonObjectClicked = (JSONObject) parent.getItemAtPosition(position);
                launchNextStep(jsonObjectClicked);
            }
        });

        hasBeenDisplayed = true;
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

        // If owner has set the odometer, the next step is to set the final odometer (after the car has been driven).
        if(transactionStatus.equals(NotificationTypeConstants.OWNER_SET_ODOMETER)){
            RequesterConfirmRent cr = new RequesterConfirmRent();
            Bundle bdl = new Bundle();
            bdl.putString("json", transactionData.toString());
            cr.setArguments(bdl);
            cr.show(getFragmentManager(), "");
        }

        // If driver has set the odometer, then the transaction is over.
        else if(transactionStatus.equals(NotificationTypeConstants.DRIVER_SET_ODOMETER)){
            Toast.makeText(this.getActivity(), "This transaction is over", Toast.LENGTH_LONG).show();
        }

        else if(transactionStatus.equals(NotificationTypeConstants.REQUEST_ACCEPTED_BY_OWNER) || transactionStatus.equals(NotificationTypeConstants.REQUEST_REFUTED_BY_OWNER)){
            launchIntentToRequestData();
        }

    }

    private void launchIntentToRequestData(){
        Intent i = new Intent(this.getActivity(), RequestData.class);
        Bundle bdl = new Bundle();
        bdl.putParcelable("user", user);
        bdl.putString("json", object.toString()); // Have to pass the JSON as a String because no implemented methods.
        i.putExtras(bdl);
        startActivity(i);
    }

    private void launchIntentToRequestSent(){
        Intent i = new Intent(this.getActivity(), AcceptedRequest.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);
        startActivity(i);
    }

    /** Next step is either show some request data or to set the odometer (when the car has been driven) or show that the transaction is over.
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
