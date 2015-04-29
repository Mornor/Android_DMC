package com.example.celien.drivemycar.tabs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.Home;
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
    private ProgressDialog progressDialog;
    private JSONArray notifications;
    private static boolean notificationInitialized;

    // ListView related stuff
    private ListAdapter adapter;
    private ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_operations, container, false);
        //init(rootView);
        return rootView;
    }

    /*
    private void init(View v){

        // Get the current user and the notifications if there is some.
        Home homeActivity = (Home)getActivity();
        user = homeActivity.getUser();

        // Get the elements on the layout
        lv = (ListView)v.findViewById(R.id.lvRequests);

        new HttpAsyncNotif(this).execute(Action.GET_NOTIFS.toString(), user.getUsername(), "true");

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
    }

    */

    /*Getters and Setter*/
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
}
