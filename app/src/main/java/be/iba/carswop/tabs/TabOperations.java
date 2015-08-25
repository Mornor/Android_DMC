package be.iba.carswop.tabs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;


public class TabOperations extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private User user;

    private ListView lvRequestStatus;
    private ProgressDialog dialog;
    private String fromDate;
    private String toDate;
    private TextView tvReceivedOrSent;
    private ImageView ivPullDownToRefresh;
    private TextView tvPullDownToRefresh;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isReceived; // True if yes.
    private List<JSONObject> requestByDate = new ArrayList<>();
    private ListAdapter adapter;

    private JSONObject object;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_operations, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View v){
        // Retrieve the user from tab hosting (Home)
        Home homeActivity   = (Home)getActivity();
        user                = homeActivity.getUser();

        Button btnReceived          = (Button)v.findViewById(R.id.btnReceived);
        Button btnSent              = (Button)v.findViewById(R.id.btnSent);
        tvReceivedOrSent            = (TextView)v.findViewById(R.id.tvReceivedOrSent);
        lvRequestStatus             = (ListView)v.findViewById(R.id.lvRequestsStatut);
        swipeRefreshLayout          = (SwipeRefreshLayout)v.findViewById(R.id.swipeRefresh);
        ivPullDownToRefresh         = (ImageView)v.findViewById(R.id.ivArrowPullDown);
        tvPullDownToRefresh         = (TextView)v.findViewById(R.id.tvSwipeDown);
        isReceived                  = true;

        // Set the listeners
        btnReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearListView(); clearListView();
                updateReceivedOrSent(true, "RECEIVED");
            }
        });

        btnSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearListView(); clearListView();
                updateReceivedOrSent(false, "SENT");
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void clearListView(){
        for(int i = 0 ; i < requestByDate.size() ; i++)
            requestByDate.remove(i);
        lvRequestStatus.setAdapter(adapter);
    }

    // Method called when the user swipe down to refresh the ListView.
    @Override
    public void onRefresh() {
        tvPullDownToRefresh.setVisibility(View.GONE);
        ivPullDownToRefresh.setVisibility(View.GONE);
        clearListView();
        if(isReceived)
            fetchReceivedTransactions();
        else
            fetchSentTransactions();
    }

    private void fetchReceivedTransactions(){
        swipeRefreshLayout.setRefreshing(true);
        clearListView();
        if(user != null)
            new HttpAsyncNotif(getActivity(), this, false).execute(Action.GET_REQUESTS_BY_DATE);
    }

    private void fetchSentTransactions(){
        swipeRefreshLayout.setRefreshing(true);
        clearListView();
        if(user != null)
            new HttpAsyncNotif(getActivity(), this, true).execute(Action.GET_REQUESTS_BY_DATE);
    }

    private void updateReceivedOrSent(boolean isReceived, String choice){
        this.isReceived = isReceived;
        tvReceivedOrSent.setText(choice);
    }

    public void onPostExecuteLoadSentRequestByDate(JSONArray array){
        try {
            Log.d("CurrentJson", array.toString());
            JSONArray withoutDuplicates = addOnlyRequestOrTransactionIntoListView(array);
            if(false/*!array.getJSONObject(0).getBoolean("success")*/)
                Log.e("Error", "JSON empty");
            else {
                // Start from 1 because 0 is the JSON to indicate if array is empty (true) or not
                for (int i = 0; i < withoutDuplicates.length(); i++) {
                    JSONObject temp = withoutDuplicates.getJSONObject(i);
                    requestByDate.add(temp);
                }
            }
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        adapter = new CustomTabOperation(this.getActivity(), requestByDate);
        lvRequestStatus.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        // Set listener to ListView
        lvRequestStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject jsonObjectClicked = (JSONObject) parent.getItemAtPosition(position);
                launchNextStep(jsonObjectClicked);
            }
        });
    }

    /**Populate the ListView to only have a the Request or the Transaction based on from_date and to_date
     * Indeed, from a certain time, the Request become a Transaction and we do not have to display it.*/
    private JSONArray addOnlyRequestOrTransactionIntoListView(JSONArray array) {
        JSONArray copyArray = array;
        JSONArray resultArray = new JSONArray();

        int i = 0, j = 0;
        try {
            while(i < array.length()){
                JSONObject temp = array.getJSONObject(i);
                j = 0;
                while(j < array.length()){
                    if(temp.getString("fromDate").equals(array.getJSONObject(j).getString("fromDate")))
                        if(array.getJSONObject(j).getBoolean("isTransaction"))
                            resultArray.put(temp);
                    j++;
                }
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Rcvd", copyArray.toString());
        Log.d("Without", resultArray.toString());
        return resultArray;
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
        switch (transactionStatus) {
            case NotificationTypeConstants.OWNER_SET_ODOMETER:
                RequesterConfirmRent cr = new RequesterConfirmRent();
                Bundle bdl = new Bundle();
                bdl.putString("json", transactionData.toString());
                cr.setArguments(bdl);
                cr.show(getFragmentManager(), "");
                break;

            // If driver has set the odometer, then the transaction is over.
            case NotificationTypeConstants.DRIVER_SET_ODOMETER:
                Toast.makeText(this.getActivity(), "This transaction is over", Toast.LENGTH_LONG).show();
                break;

            case NotificationTypeConstants.REQUEST_ACCEPTED_BY_OWNER:

            case NotificationTypeConstants.REQUEST_REFUTED_BY_OWNER:
                launchIntentToRequestData();
                break;
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

    private void launchIntentToAcceptedRequest(){
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
    private void launchNextStep(JSONObject object) {
        Log.d("Object ", object.toString());
        this.object = object;
        String status = "";
        try {
            fromDate = object.getString("fromDate");
            toDate = object.getString("toDate");
            status = object.getString("status");
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        if (status.equals(NotificationTypeConstants.WAITING_FOR_ANSWER_OF_OWNER) && !isReceived)
            Toast.makeText(this.getActivity(), "Status : Request has been sent", Toast.LENGTH_LONG).show();
        if (status.equals(NotificationTypeConstants.REQUEST_ACCEPTED_BY_OWNER) && !isReceived)
            launchIntentToRequestData();
        if (status.equals(NotificationTypeConstants.REQUEST_ACCEPTED_BY_OWNER) && isReceived)
            Toast.makeText(this.getActivity(), "Status : Waiting for the driver's confirmation", Toast.LENGTH_LONG).show();
        if (status.equals(NotificationTypeConstants.WAITING_FOR_ANSWER_OF_OWNER) && isReceived)
            lauchIntentToRequestReceived();
        if(status.equals(NotificationTypeConstants.REQUEST_ACCEPTED_BY_BOTH_SIDES) && !isReceived){
            Toast.makeText(this.getActivity(), "Status : Request has been accepted", Toast.LENGTH_LONG).show();
            new HttpAsyncTransaction(this.getActivity(), this).execute(Action.CHECK_TRANSACTION_STATUS);
        }

        if(status.equals(NotificationTypeConstants.REQUEST_ACCEPTED_BY_BOTH_SIDES) && isReceived)
            launchIntentToAcceptedRequest();
    }


    private void lauchIntentToRequestReceived(){
        Intent i = new Intent(this.getActivity(), RequestReceived.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);
        startActivity(i);
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

    public ProgressDialog getProgressDialog() {
        return dialog;
    }

    public void setProgressDialog(ProgressDialog dialog) {
        this.dialog = dialog;
    }
}
