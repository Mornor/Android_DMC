package com.example.celien.drivemycar.core;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.http.HttpAsyncNotif;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelectOwner extends ActionBarActivity {

    private User user;
    private JSONObject jsonObject; // Contains the 2 dates (fromDate and toDate)
    private String fromDate;
    private String toDate;
    private ListView lv;
    private ListAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_owner);
        init();
    }

    private void init(){

        // Get the current user and the json clicked in RequestData (which contains the dates of the wanted request)
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null){
            this.user = currentUser;
            try {
                this.jsonObject = new JSONObject(getIntent().getStringExtra("json"));
                this.fromDate   = this.jsonObject.getString("fromDate");
                this.toDate     = this.jsonObject.getString("toDate");
            } catch (JSONException e) {
                Log.e(e.getClass().getName(), "JSONException", e);
            }
        }

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Request Data");

        // Get the items on the layout
        lv = (ListView)findViewById(R.id.lvOwners);

        getAgreedOwners();
    }

    private void getAgreedOwners(){
        new HttpAsyncNotif(this).execute(Action.GET_AGREED_OWNERS.toString(), user.getUsername(), fromDate, toDate);
    }

    public void onOnPostAgreedOnwers(JSONArray array){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_owner, menu);
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

    /*Getters and Setters*/
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
}
