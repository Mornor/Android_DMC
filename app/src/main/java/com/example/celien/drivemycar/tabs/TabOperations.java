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
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomTabOperation;
import com.example.celien.drivemycar.core.AcceptOwner;
import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.core.RequestReceived;
import com.example.celien.drivemycar.http.HttpAsyncNotif;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TabOperations extends Fragment {

    private User user;

    private ListAdapter adapter;
    private Button btnRequests;
    private ListView lvRequestStatus;
    private ProgressDialog progressDialog;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_operations, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View v){
        // Retrieve the user from tab hosting (Home)
        Home homeActivity   = (Home)getActivity();
        user                = homeActivity.getUser();

        btnRequests     = (Button)v.findViewById(R.id.btnCheckRequests);
        lvRequestStatus = (ListView)v.findViewById(R.id.lvRequestsStatut);

        btnRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lauchIntentToRequestReceived();
            }
        });

        loadUserRequestByDate();
    }

    private void loadUserRequestByDate(){
        if(user != null)
            new HttpAsyncNotif(getActivity(), this).execute(Action.GET_REQUEST_DATA.toString(), user.getUsername());
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

        adapter = new CustomTabOperation(this.getActivity(), list, this);
        lvRequestStatus.setAdapter(adapter);

        // Set listener to ListView
        lvRequestStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject jsonObjectClicked = (JSONObject)parent.getItemAtPosition(position);
                launchIntentToAcceptOwner(jsonObjectClicked);
            }
        });
    }

    private void launchIntentToAcceptOwner(JSONObject object){
        Intent i = new Intent(this.getActivity(), AcceptOwner.class);
        Bundle bdl = new Bundle();
        bdl.putParcelable("user", user);
        bdl.putString("json", object.toString()); // Have to pass the JSON as a String because no implemented methods.
        i.putExtras(bdl);
        startActivity(i);
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
}
