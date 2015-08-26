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
import be.iba.carswop.fragment.OwnerConfirmRent;
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
import java.util.Calendar;
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
            if(withoutDuplicates.length() == 0)
                Toast.makeText(this.getActivity(), "Nothing to show", Toast.LENGTH_SHORT).show();
            else {
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
        JSONArray resultArray = new JSONArray();
        JSONArray copyArray = new JSONArray();

        try {
            // Create a copy of the original Array
            for (int i = 0; i < array.length(); i++)
                copyArray.put(array.getJSONObject(i));

            // Put every items with the same date.
            int i = 0, j = 0;
            while(i < array.length()){
                while(j < copyArray.length()){
                    if(i!=j && array.getJSONObject(i).getString("fromDate").equals(copyArray.getJSONObject(j).getString("fromDate")))
                        resultArray.put(copyArray.getJSONObject(j));
                    j++;
                }
                i++; j = 0;
            }

            // Put every single items
            // If no duplicates, put everything in resultArray
            if(resultArray.length() == 0){
                for(int p = 0 ; p < array.length() ; p++){
                    resultArray.put(array.getJSONObject(p));
                }
            }

            int k = 0, l = 0;
            while(k < array.length()){
                while (l < resultArray.length()){
                    if(k!=l && !isIntPresentInArray(resultArray, array.getJSONObject(k).getInt("id")))
                        resultArray.put(array.getJSONObject(k));
                    l++;
                }
                k++; l = 0;
            }

        }catch (JSONException e){
            Log.e(this.getClass().getName(), "JSONException", e);
        }

        return resultArray;
    }

    private boolean isIntPresentInArray(JSONArray array, int toFind){
        try{
            for(int i = 0 ; i < array.length() ; i++){
                if(array.getJSONObject(i).getInt("id") == toFind)
                    return true;
            }
            return false;
        }catch (JSONException e){
            Log.e(this.getClass().getName(), "JSONException", e);
        }



        return true;
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
        this.object = object;
        String status = "";
        boolean isTransaction = false;
        try {
            fromDate = object.getString("fromDate");
            toDate = object.getString("toDate");
            status = object.getString("status");
            Log.d("Status", status);
            isTransaction = object.getBoolean("isTransaction");
            Log.d("isTransaction", String.valueOf(isTransaction));
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
        if(!isTransaction && isReceived)
            launchConfirmDialogIfNecessary(fromDate, status);
    }

    private void launchConfirmDialogIfNecessary(String fromDate, String status){
        // Compare fromDate of Transaction to the currentDate
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf((c.get(Calendar.MONTH) + 1));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));

        // This way if month == 8, we get 08
        if(month.length() == 1)
            month = "0"+String.valueOf(month).charAt(0);
        if(day.length() == 1)
            day = "0"+String.valueOf(month).charAt(0);
        String currentDate = (year+"-"+month+"-"+day);


        if(status.equals(NotificationTypeConstants.DRIVER_WAITING_FOR_OWNER_KEY) && currentDate.equals(fromDate)){
            OwnerConfirmRent ownerConfirmRent = new OwnerConfirmRent();
            Bundle bdl = new Bundle();
            bdl.putString("json", object.toString());
            ownerConfirmRent.setArguments(bdl);
            ownerConfirmRent.show(this.getActivity().getSupportFragmentManager(), "");
        }

        else if(status.equals(NotificationTypeConstants.DRIVER_WAITING_FOR_OWNER_KEY) && !currentDate.equals(fromDate))
            Toast.makeText(this.getActivity(), "Either this transaction happened or will happen.", Toast.LENGTH_LONG).show();

        else
            Toast.makeText(this.getActivity(), "Nothing left to do with this one.", Toast.LENGTH_SHORT).show();
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
