package com.example.celien.drivemycar.core;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomFragmentTabOperations;
import com.example.celien.drivemycar.http.HttpAsyncNotif;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestReceived extends ActionBarActivity {

    // ListView related stuff
    private ListAdapter adapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_received);
        init();
    }

    private void init(){
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
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        adapter = new CustomFragmentTabOperations(this.getActivity(), list, this);
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_request_received, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
