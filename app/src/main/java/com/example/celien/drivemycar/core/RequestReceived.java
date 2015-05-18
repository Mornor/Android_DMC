package com.example.celien.drivemycar.core;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomRequestReceived;
import com.example.celien.drivemycar.http.HttpAsyncNotif;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RequestReceived extends ActionBarActivity {

    private User user;
    private ListView lv;
    private TextView tvNoRequestsFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_received);
        init();
    }

    private void init(){
        // Get the current user and the notifications if there is some.
        User currentUser = getIntent().getParcelableExtra("user");
        if(currentUser != null)
            this.user = currentUser;

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Request(s) Received");

        // Get the elements from the layout
        lv                  = (ListView)findViewById(R.id.lvRequests);
        tvNoRequestsFound   = (TextView)findViewById(R.id.tvNoRequests);

        new HttpAsyncNotif(this).execute(Action.GET_NOTIFS.toString(), user.getUsername(), "true");
    }

    public void onPostExecuteLoadNotification(JSONArray array){
        boolean isArrayEmpty = false;
        try{
            isArrayEmpty = array.getJSONObject(0).getBoolean("isArrayEmpty");
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        // If there is no request in DB
        if(isArrayEmpty){
            tvNoRequestsFound.setVisibility(View.VISIBLE);
            tvNoRequestsFound.setText("No requests found in DB");
            tvNoRequestsFound.setTextColor(Color.RED);
        }else{
            createListView(array);
        }
    }

    private void createListView(JSONArray array){
        List<JSONObject> list = new ArrayList<>();
        try {
            // Start from 1 because 0 is the JSON to indicate if array is empty (true) or not
            for(int i = 1 ; i < array.length() ; i++){
                JSONObject temp = array.getJSONObject(i);
                list.add(temp);
            }
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        ListAdapter adapter = new CustomRequestReceived(this, list, this);
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
